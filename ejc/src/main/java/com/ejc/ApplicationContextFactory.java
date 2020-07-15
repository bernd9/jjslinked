package com.ejc;

import com.ejc.processor.*;

import java.util.Set;

public interface ApplicationContextFactory {
    ApplicationContext createContext();

    Set<SingletonLoaderBase> getSingletonLoaders();

    Set<SingletonLoaderBase> getImplementationLoaders();

    Set<InjectorBase> getInjectors();

    Set<MultiInjectorBase> getMultiInjectors();

    Set<SystemPropertyInjectorBase> getPropertyInjectors();

    Set<InitializerBase> getInitializers();
}
