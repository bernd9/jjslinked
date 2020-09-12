package com.ejc.api.context;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.ejc.api.config.Config;
import com.ejc.processor.ApplicationContextImpl;
import com.ejc.util.InstanceUtils;
import com.ejc.util.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class ApplicationContextFactoryBase implements ApplicationContextFactory {
    private final Set<ClassReference> beanClasses = new HashSet<>();
    private final Set<ClassReference> configurationClasses = new HashSet<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();
    private final Set<Object> loadedBeans = new HashSet<>();
    private final Set<Object> loadedConfigurations = new HashSet<>();
    private final Map<Class<?>, Set<Object>> singletonCache = new HashMap<>();
    private final Map<Class<?>, Set<Object>> configurationCache = new HashMap<>();
    private final Set<SingleValueInjector> singleValueInjectors = new HashSet<>();
    private final Set<MultiValueInjector> multiValueInjectors = new HashSet<>();
    private final Set<InitInvoker> initInvokersForSingleton = new HashSet<>();
    private final Set<InitInvoker> initInvokersForConfiguration = new HashSet<>();
    private final Set<ConfigValueInjector> configValueInjectorsForSingleton = new HashSet<>();
    private final Set<ConfigValueInjector> configValueInjectorsForConfiguration = new HashSet<>();
    private final Set<LoadBeanMethodInvoker> loadBeanMethodInvokers = new HashSet<>();

    @Override
    public ApplicationContext createContext() {
        ApplicationContextImpl applicationContext = new ApplicationContextImpl();
        loadedBeans.add(applicationContext);
        createConfigurations();
        doConfigParamInjectionConfigurations();
        invokeInitializersOnConfigurations();
        createBeans();
        doConfigParamInjectionBeans();
        doDependencyInjection();
        invokeInitializersOnBeans();
        applicationContext.addBeans(loadedBeans);
        ApplicationContext.instance = applicationContext;
        return applicationContext;
    }


    public void removeBeanClasses(Collection<ClassReference> classes) {
        beanClasses.removeAll(classes);
    }

    public void append(ApplicationContextFactory factory) {
        ApplicationContextFactoryBase factoryBase = (ApplicationContextFactoryBase) factory;
        beanClasses.addAll(factoryBase.getBeanClasses());
        configurationClasses.addAll(factoryBase.getConfigurationClasses());
        loadBeanMethodInvokers.addAll(factoryBase.getLoadBeanMethodInvokers());
        classesToReplace.addAll(factoryBase.getClassesToReplace());
        loadedBeans.addAll(factoryBase.getLoadedBeans());
        loadedConfigurations.addAll(factoryBase.getLoadedConfigurations());
        singleValueInjectors.addAll(factoryBase.getSingleValueInjectors());
        multiValueInjectors.addAll(factoryBase.getMultiValueInjectors());
        initInvokersForSingleton.addAll(factoryBase.getInitInvokersForSingleton());
        initInvokersForConfiguration.addAll(factoryBase.getInitInvokersForConfiguration());
        configValueInjectorsForSingleton.addAll(factoryBase.getConfigValueInjectorsForSingleton());
        configValueInjectorsForConfiguration.addAll(factoryBase.getConfigValueInjectorsForConfiguration());
    }

    private void createBeans() {
        loadedBeans.addAll(loadBeanMethodInvokers.stream()
                .map(invoker -> invoker.doInvoke(this))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        loadedBeans.addAll(beanClasses.stream()
                .map(this::createInstance).collect(Collectors.toSet()));
    }

    private void createConfigurations() {
        loadedConfigurations.addAll(configurationClasses.stream()
                .map(this::createInstance).collect(Collectors.toSet()));
    }

    private void doDependencyInjection() {
        singleValueInjectors.forEach(injector -> injector.doInject(this));
        multiValueInjectors.forEach(injector -> injector.doInject(this));
    }

    private void doConfigParamInjectionBeans() {
        configValueInjectorsForSingleton.forEach(configValueInjector -> configValueInjector.doInject(this::getBeans));
    }

    private void doConfigParamInjectionConfigurations() {
        configValueInjectorsForConfiguration.forEach(configValueInjector -> configValueInjector.doInject(this::getConfigurations));
    }

    private void invokeInitializersOnBeans() {
        initInvokersForSingleton.forEach(invoker -> invoker.doInvoke(this::getBeans));
    }

    private void invokeInitializersOnConfigurations() {
        initInvokersForConfiguration.forEach(invoker -> invoker.doInvoke(this::getConfigurations));
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
        return (Set<T>) singletonCache.computeIfAbsent(c, this::findMatchingBeans);
    }

    <T> Set<T> getConfigurations(Class<T> c) {
        return (Set<T>) configurationCache.computeIfAbsent(c, this::findMatchingConfiguration);
    }

    @SuppressWarnings("unused")
    protected void addBeanClass(ClassReference c) {
        beanClasses.add(c);
    }

    @SuppressWarnings("unused")
    protected void addConfigurationClass(ClassReference c) {
        configurationClasses.add(c);
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
    protected void addInitMethodForSingleton(ClassReference declaringClass, String methodName) {
        initInvokersForSingleton.add(new InitInvoker(declaringClass, methodName));
    }

    @SuppressWarnings("unused")
    protected void addLoadBeanMethod(ClassReference declaringClass, String methodName) {
        loadBeanMethodInvokers.add(new LoadBeanMethodInvoker(declaringClass, methodName));
    }

    @SuppressWarnings("unused")
    protected void addInitMethodForConfiguration(ClassReference declaringClass, String methodName) {
        initInvokersForConfiguration.add(new InitInvoker(declaringClass, methodName));
    }

    @SuppressWarnings("unused")
    protected void addConfigValueFieldInSingleton(ClassReference declaringClass, String fieldName, Class<?> fieldType, String key, String defaultValue) {
        configValueInjectorsForSingleton.add(new ConfigValueInjector(declaringClass, fieldName, fieldType, key, defaultValue));
    }


    @SuppressWarnings("unused")
    protected void addConfigValueFieldInConfiguration(ClassReference declaringClass, String fieldName, Class<?> fieldType, String key, String defaultValue) {
        configValueInjectorsForConfiguration.add(new ConfigValueInjector(declaringClass, fieldName, fieldType, key, defaultValue));
    }

    private Set<Object> findMatchingBeans(Class<?> c) {
        return loadedBeans.stream()
                .filter(c::isInstance)
                .collect(Collectors.toSet());
    }

    private Set<Object> findMatchingConfiguration(Class<?> c) {
        return loadedConfigurations.stream()
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
            doInjectFieldValue(bean, ReflectionUtils.getField(bean, fieldName), factory);
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
class ConfigValueInjector {

    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final String key;
    private final String defaultValue;

    void doInject(Function<Class<?>, Set<?>> selectFunction) {
        selectFunction.apply(declaringClass.getClazz()).forEach(bean -> doInject(bean));
    }

    private void doInject(Object bean) {
        try {
            Field field = ReflectionUtils.getField(bean, fieldName);
            field.setAccessible(true);
            field.set(bean, Config.getProperty(key, fieldType, defaultValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

@RequiredArgsConstructor
class InitInvoker {
    private final ClassReference declaringClass;
    private final String methodName;

    void doInvoke(Function<Class<?>, Set<?>> selectFunction) {
        selectFunction.apply(declaringClass.getClazz()).forEach(bean -> doInvokeMethod(bean));
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

@RequiredArgsConstructor
class LoadBeanMethodInvoker {
    private final ClassReference declaringClass;
    private final String methodName;

    Collection<Object> doInvoke(ApplicationContextFactoryBase factoryBase) {
        return factoryBase.getConfigurations(declaringClass.getClazz()).stream()
                .map(this::doInvokeMethod)
                .collect(Collectors.toSet());
    }

    private Object doInvokeMethod(Object bean) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


