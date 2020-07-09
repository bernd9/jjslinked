package com.ejc.processor;

import com.ejc.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

public class ApplicationContextFactoryBase {
    private final Set<SingeltonLoaderBase> singletonLoaders = new HashSet<>();
    private final Set<InjectorBase> injectors = new HashSet<>();

    public void addSingletonLoader(String loaderClassName) {
        singletonLoaders.add((SingeltonLoaderBase) BeanUtils.createInstance(loaderClassName));
    }

    public void addInjector(String injectorClassName) {
        injectors.add((InjectorBase) BeanUtils.createInstance(injectorClassName));
    }

    ApplicationContext createContext() {
        ApplicationContext context = new ApplicationContextImpl();
        singletonLoaders.stream().map(SingeltonLoaderBase::load).forEach(context::addBean);
        injectors.forEach(injector -> injector.doInject(context));
        return context;
    }


}
