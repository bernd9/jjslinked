package com.ejc.processor;

import com.ejc.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

public class ApplicationContextFactoryBase {
    private final Set<SingletonLoaderBase> singletonLoaders = new HashSet<>();
    private final Set<InjectorBase> injectors = new HashSet<>();
    private final Set<MultiInjectorBase> multiInjectors = new HashSet<>();
    private final Set<InitializerBase> initializers = new HashSet<>();

    public void addSingletonLoader(String loaderClassName) {
        singletonLoaders.add((SingletonLoaderBase) BeanUtils.createInstance(loaderClassName));
    }

    public void addInjector(String injectorClassName) {
        injectors.add((InjectorBase) BeanUtils.createInstance(injectorClassName));
    }

    public void addMultiInjector(String injectorClassName) {
        multiInjectors.add((MultiInjectorBase) BeanUtils.createInstance(injectorClassName));
    }

    public void addInitializer(String initializerName) {
        initializers.add((InitializerBase) BeanUtils.createInstance(initializerName));
    }

    public ApplicationContext createContext() {
        ApplicationContextImpl context = new ApplicationContextImpl();
        singletonLoaders.stream().map(SingletonLoaderBase::load).forEach(context::addBean);
        injectors.forEach(injector -> injector.doInject(context));
        multiInjectors.forEach(injector -> injector.doInject(context));
        initializers.parallelStream().forEach(initializer -> initializer.invokeInit(context));
        return context;
    }


}
