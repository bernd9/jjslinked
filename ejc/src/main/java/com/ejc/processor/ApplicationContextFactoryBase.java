package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter(AccessLevel.PACKAGE)
public class ApplicationContextFactoryBase implements ApplicationContextFactory {
    private final Set<SingletonLoaderBase> singletonLoaders = new HashSet<>();
    private final Set<InjectorBase> injectors = new HashSet<>();
    private final Set<MultiInjectorBase> multiInjectors = new HashSet<>();
    private final Set<InitializerBase> initializers = new HashSet<>();
    private final Set<SystemPropertyInjectorBase> propertyInjectors = new HashSet<>();
    //private final Set<ApplicationContextFactoryBase> contextFactories = new HashSet<>();

    @SuppressWarnings("unused")
    public void addSingletonLoader(Class<? extends SingletonLoaderBase> loaderClass) {
        singletonLoaders.add((SingletonLoaderBase) BeanUtils.createInstance(loaderClass));
    }

    @SuppressWarnings("unused")
    public void addInjector(Class<? extends InjectorBase> injectorClass) {
        injectors.add((InjectorBase) BeanUtils.createInstance(injectorClass));
    }

    @SuppressWarnings("unused")
    public void addMultiInjector(Class<? extends MultiInjectorBase> injectorClass) {
        multiInjectors.add((MultiInjectorBase) BeanUtils.createInstance(injectorClass));
    }

    @SuppressWarnings("unused")
    public void addPropertyInjector(Class<? extends SystemPropertyInjectorBase> propertyInjectorClass) {
        propertyInjectors.add((SystemPropertyInjectorBase) BeanUtils.createInstance(propertyInjectorClass));
    }

    @SuppressWarnings("unused")
    public void addInitializer(Class<? extends InitializerBase> initializerClass) {
        initializers.add((InitializerBase) BeanUtils.createInstance(initializerClass));
    }

    /*
    @SuppressWarnings("unused")
    public void addApplication(Class<? extends ApplicationContextFactoryBase> factory) {
        contextFactories.add((ApplicationContextFactoryBase) BeanUtils.createInstance(factory));
    }
    */

    @Override
    public ApplicationContext createContext() {
        ApplicationContextImpl context = new ApplicationContextImpl();
        //contextFactories.stream().map(ApplicationContextFactoryBase::createContext).map(ApplicationContext::getBeans).forEach(context::addBeans);
        singletonLoaders.stream().map(SingletonLoaderBase::load).forEach(context::addBean);
        propertyInjectors.forEach(injector -> injector.doInject(context));
        injectors.forEach(injector -> injector.doInject(context));
        multiInjectors.forEach(injector -> injector.doInject(context));
        initializers.parallelStream().forEach(initializer -> initializer.invokeInit(context));
        return context;
    }


}
