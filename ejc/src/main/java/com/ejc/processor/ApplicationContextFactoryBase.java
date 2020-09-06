package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.ejc.util.InstanceUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ApplicationContextFactoryBase implements ApplicationContextFactory {
    private final Set<ClassReference> beanClasses = new HashSet<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();
    private final Set<Object> loadedBeans = new HashSet<>();
    private final Map<Class<?>, Set<Object>> cache = new HashMap<>();

    private final Set<SingleValueInjector> singleValueInjectors = new HashSet<>();
    private final Set<MultiValueInjector> multiValueInjectors = new HashSet<>();
    private final Set<InitInvoker> initInvokers = new HashSet<>();

    @Override
    public ApplicationContext createContext() {
        createBeans();
        doConfigParamInjection();
        doDependencyInjection();
        invokeInitializers();
        return new ApplicationContextImpl(loadedBeans);
    }

    public void removeBeanClasses(Collection<ClassReference> classes) {
        beanClasses.removeAll(classes);
    }

    public void append(ApplicationContextFactory factory) {
        ApplicationContextFactoryBase factoryBase = (ApplicationContextFactoryBase) factory;
        beanClasses.addAll(factoryBase.getBeanClasses());
        classesToReplace.addAll(factoryBase.getClassesToReplace());
        loadedBeans.addAll(factoryBase.getLoadedBeans());
        singleValueInjectors.addAll(factoryBase.getSingleValueInjectors());
        multiValueInjectors.addAll(factoryBase.getMultiValueInjectors());
        initInvokers.addAll(factoryBase.getInitInvokers());
    }

    private void createBeans() {
        loadedBeans.addAll(beanClasses.stream()
                .map(this::createInstance).collect(Collectors.toSet()));
    }

    private void doDependencyInjection() {
        singleValueInjectors.forEach(injector -> injector.doInject(this));
        multiValueInjectors.forEach(injector -> injector.doInject(this));
    }

    private void doConfigParamInjection() {
    }

    private void invokeInitializers() {
        initInvokers.forEach(invoker -> invoker.doInvoke(this));
    }


    <T> T getBean(Class<T> c) {
        List<Object> result = new ArrayList<>(getBeans(c));
        switch (result.size()) {
            case 0:
                throw new IllegalArgumentException("no bean of type " + c.getName());
            case 1:
                return (T) result.get(0);
            default:
                throw new IllegalStateException("ambigious : " + c.getName() + ", matching beans : " + result.stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }


    <T> Set<T> getBeans(Class<T> c) {
        return (Set<T>) cache.computeIfAbsent(c, this::findMatchingBeans);
    }


    protected void addBeanClass(ClassReference c) {
        beanClasses.add(c);
    }

    @SuppressWarnings("unused")
    protected <B> void addImplementation(ClassReference base, ClassReference impl) {
        beanClasses.remove(base); // No matter if not exists
        beanClasses.add(impl);
    }

    @SuppressWarnings("unused")
    protected void addBeanClassForReplacement(ClassReference beanClass, ClassReference classToReplace) {
        addBeanClass(beanClass);
        classesToReplace.add(classToReplace);
    }

    @SuppressWarnings("unused")
    protected void addSingleValueDependency(ClassReference declaringClass, String fieldName, ClassReference fieldType) {
        singleValueInjectors.add(new SingleValueInjector(declaringClass, fieldName, fieldType));
    }

    @SuppressWarnings("unused")
    protected void addMultiValueDependency(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference elementType) {
        multiValueInjectors.add(new MultiValueInjector(declaringClass, fieldName, fieldType, elementType));
    }

    @SuppressWarnings("unused")
    protected void addInitMethod(ClassReference declaringClass, String methodName) {
        initInvokers.add(new InitInvoker(declaringClass, methodName));
    }


    private Set<Object> findMatchingBeans(Class<?> c) {
        return loadedBeans.stream()
                .filter(c::isInstance)
                .collect(Collectors.toSet());
    }


    private <T> T createInstance(ClassReference ref) {
        return (T) InstanceUtils.createInstance(ref.getClazz());
    }

}

@RequiredArgsConstructor
abstract class InjectorBase {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final ClassReference fieldType;

    void doInject(ApplicationContextFactoryBase factory) {
        factory.getBeans(declaringClass.getClazz()).forEach(bean -> doInject(bean, factory));
    }

    private void doInject(Object bean, ApplicationContextFactoryBase factory) {
        try {
            doInjectFieldValue(bean, getField(bean), factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doInjectFieldValue(Object bean, Field field, ApplicationContextFactoryBase factory) {
        try {
            field.setAccessible(true);
            field.set(bean, getFieldValue(fieldType.getClazz(), factory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory);

    // TODO test for field in superclass
    private Field getField(Object bean) throws NoSuchFieldException {
        for (Class<?> c = bean.getClass(); c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {

            }
        }
        throw new NoSuchFieldException(fieldName);
    }

}


class SingleValueInjector extends InjectorBase {

    public SingleValueInjector(ClassReference declaringClass, String fieldName, ClassReference fieldType) {
        super(declaringClass, fieldName, fieldType);
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        return factory.getBean(fieldType);
    }
}

class MultiValueInjector extends InjectorBase {

    private final ClassReference fieldValueType;

    public MultiValueInjector(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference fieldValueType) {
        super(declaringClass, fieldName, new ClassReference(fieldType));
        this.fieldValueType = fieldValueType;
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        Set<Object> set = factory.getBeans((Class<Object>) fieldValueType.getClazz());
        if (fieldType.isAssignableFrom(Set.class)) {
            return set;
        }
        if (fieldType.isArray()) {
            return set.toArray(new Object[set.size()]);
        }
        if (fieldType.isAssignableFrom(List.class)) {
            return Collections.unmodifiableList(new ArrayList<>(set));
        }
        if (fieldType.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>(set);
        }
        throw new IllegalStateException("unsupported collection type: " + fieldType);

    }
}

@RequiredArgsConstructor
class InitInvoker {
    private final ClassReference declaringClass;
    private final String methodName;

    void doInvoke(ApplicationContextFactoryBase factory) {
        factory.getBeans(declaringClass.getClazz()).forEach(bean -> doInvokeMethod(bean));
    }

    private void doInvokeMethod(Object bean) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
