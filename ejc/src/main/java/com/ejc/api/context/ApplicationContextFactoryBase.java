package com.ejc.api.context;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.ejc.processor.ApplicationContextImpl;
import com.ejc.processor.UsedInGeneratedCode;
import com.ejc.util.ClassUtils;
import lombok.Getter;

import java.util.*;
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
    private final Set<InitMethodInvoker> initMethodInvokersForSingleton = new HashSet<>();
    private final Set<InitMethodInvoker> initMethodInvokersForConfiguration = new HashSet<>();
    private final Set<ConfigValueInjector> configValueInjectorsForSingleton = new HashSet<>();
    private final Set<ConfigValueInjector> configValueInjectorsForConfiguration = new HashSet<>();
    private final Set<BeanFactoryMethodInvoker> loadBeanMethodInvokers = new HashSet<>();

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

    @Override
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
        initMethodInvokersForSingleton.addAll(factoryBase.getInitMethodInvokersForSingleton());
        initMethodInvokersForConfiguration.addAll(factoryBase.getInitMethodInvokersForConfiguration());
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
        initMethodInvokersForSingleton.forEach(invoker -> invoker.doInvoke(this::getBeans));
    }

    private void invokeInitializersOnConfigurations() {
        initMethodInvokersForConfiguration.forEach(invoker -> invoker.doInvoke(this::getConfigurations));
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

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addBeanClass(ClassReference c) {
        beanClasses.add(c);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addConfigurationClass(ClassReference c) {
        configurationClasses.add(c);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected <B> void addImplementation(ClassReference base, ClassReference impl) {
        beanClasses.remove(base); // No matter if not exists
        beanClasses.add(impl);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addBeanClassForReplacement(ClassReference beanClass, ClassReference classToReplace) {
        addBeanClass(beanClass);
        classesToReplace.add(classToReplace);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addSingleValueDependency(ClassReference declaringClass, String fieldName, ClassReference fieldType) {
        singleValueInjectors.add(new SingleValueInjector(declaringClass, fieldName, fieldType));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addMultiValueDependency(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference elementType) {
        multiValueInjectors.add(new MultiValueInjector(declaringClass, fieldName, fieldType, elementType));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addInitMethodForSingleton(ClassReference declaringClass, String methodName) {
        initMethodInvokersForSingleton.add(new InitMethodInvoker(declaringClass, methodName));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addLoadBeanMethod(ClassReference declaringClass, String methodName) {
        loadBeanMethodInvokers.add(new BeanFactoryMethodInvoker(declaringClass, methodName));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addInitMethodForConfiguration(ClassReference declaringClass, String methodName) {
        initMethodInvokersForConfiguration.add(new InitMethodInvoker(declaringClass, methodName));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addConfigValueFieldInSingleton(ClassReference declaringClass, String fieldName, Class<?> fieldType, String key, String defaultValue) {
        configValueInjectorsForSingleton.add(new ConfigValueInjector(declaringClass, fieldName, fieldType, key, defaultValue));
    }

    @UsedInGeneratedCode
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
        return (T) ClassUtils.createInstance(ref.getReferencedClass());
    }

}


