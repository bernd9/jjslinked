package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ApplicationContextFactoryBase implements ApplicationContextFactory {
    private final Set<Class<?>> beanClasses = new HashSet<>();
    private final Set<Class<?>> classesToReplace = new HashSet<>();
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

    public void removeBeanClasses(Collection<Class<?>> classes) {
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
        loadedBeans.addAll(beanClasses.stream().map(this::createInstance).collect(Collectors.toSet()));
    }

    private void doDependencyInjection() {
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


    protected void addBeanClasses(Class<?>[] c) {
        beanClasses.addAll(Arrays.asList(c));
    }

    protected void addBeanClass(Class<?> c) {
        beanClasses.add(c);
    }

    protected void addBeanClassForReplacement(Class<?> beanClass, Class<?> classToReplace) {
        addBeanClass(beanClass);
        classesToReplace.add(classToReplace);
    }

    protected void addSingleValueDependency(Class<?> declaringClass, String fieldName, Class<?> fieldType) {
        singleValueInjectors.add(new SingleValueInjector(declaringClass, fieldName, fieldType));
    }

    protected void addMultiValueDependency(Class<?> declaringClass, String fieldName, Class<?> fieldType, Class<?> elementType) {
        multiValueInjectors.add(new MultiValueInjector(declaringClass, fieldName, fieldType, elementType));
    }

    protected void addInitMethod(Class<?> declaringClass, String methodName) {
        initInvokers.add(new InitInvoker(declaringClass, methodName));
    }


    private Set<Object> findMatchingBeans(Class<?> c) {
        return loadedBeans.stream()
                .filter(c::isInstance)
                .collect(Collectors.toSet());
    }


    private <T> T createInstance(Class<T> c) {
        return (T) InstanceUtils.createInstance(c);
    }

}

@RequiredArgsConstructor
abstract class InjectorBase {
    private final Class<?> declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;

    void doInject(ApplicationContextFactoryBase factory) {
        factory.getBeans(declaringClass).forEach(bean -> doInject(bean, factory));
    }

    private void doInject(Object bean, ApplicationContextFactoryBase factory) {
        try {
            doInjectFieldValue(bean, bean.getClass().getDeclaredField(fieldName), factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doInjectFieldValue(Object bean, Field field, ApplicationContextFactoryBase factory) {
        try {
            field.setAccessible(true);
            field.set(bean, getFieldValue(fieldType, factory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory);

}


class SingleValueInjector extends InjectorBase {

    public SingleValueInjector(Class<?> declaringClass, String fieldName, Class<?> fieldType) {
        super(declaringClass, fieldName, fieldType);
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        return factory.getBean(fieldType);
    }
}

class MultiValueInjector extends InjectorBase {

    private final Class<?> fieldValueType;

    public MultiValueInjector(Class<?> declaringClass, String fieldName, Class<?> fieldType, Class<?> fieldValueType) {
        super(declaringClass, fieldName, fieldType);
        this.fieldValueType = fieldValueType;
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        Set<Object> set = factory.getBeans((Class<Object>) fieldValueType);
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
    private final Class<?> declaringClass;
    private final String methodName;

    void doInvoke(ApplicationContextFactoryBase factory) {
        factory.getBeans(declaringClass).forEach(bean -> doInvokeMethod(bean));
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
