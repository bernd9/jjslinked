package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ApplicationContextFactoryBase implements ApplicationContextFactory {
    private final Set<SingletonLoaderBase> singletonLoaders = new HashSet<>();
    private final Set<InjectorBase> injectors = new HashSet<>();
    private final Set<MultiInjectorBase> multiInjectors = new HashSet<>();
    private final Set<InitializerBase> initializers = new HashSet<>();
    private final Set<SystemPropertyInjectorBase> propertyInjectors = new HashSet<>();

    @SuppressWarnings("unused")
    public void addSingletonLoader(Class<? extends SingletonLoaderBase> loaderClass) {
        singletonLoaders.add((SingletonLoaderBase) InstanceUtils.createInstance(loaderClass));
    }

    @SuppressWarnings("unused")
    public void addInjector(Class<? extends InjectorBase> injectorClass) {
        injectors.add((InjectorBase) InstanceUtils.createInstance(injectorClass));
    }

    @SuppressWarnings("unused")
    public void addMultiInjector(Class<? extends MultiInjectorBase> injectorClass) {
        multiInjectors.add((MultiInjectorBase) InstanceUtils.createInstance(injectorClass));
    }

    @SuppressWarnings("unused")
    public void addPropertyInjector(Class<? extends SystemPropertyInjectorBase> propertyInjectorClass) {
        propertyInjectors.add((SystemPropertyInjectorBase) InstanceUtils.createInstance(propertyInjectorClass));
    }

    @SuppressWarnings("unused")
    public void addInitializer(Class<? extends InitializerBase> initializerClass) {
        initializers.add((InitializerBase) InstanceUtils.createInstance(initializerClass));
    }

    @Override
    public ApplicationContext createContext() {
        ApplicationContextImpl context = new ApplicationContextImpl();
        List<ApplicationContextFactory> factories = loadFactories().collect(Collectors.toList());
        addSingletons(context, factories);
        doInjection(context, factories);
        doMultiInjection(context, factories);
        doPropertyInjection(context, factories);
        doInitialize(context, factories);
        ApplicationContext.instance = context;
        return context;
    }

    private Stream<ApplicationContextFactory> loadFactories() {
        ServiceLoader<ApplicationContextFactory> loader = ServiceLoader.load(ApplicationContextFactory.class);
        return loader.stream().map(ServiceLoader.Provider::get);
    }

    private void addSingletons(ApplicationContextImpl context, List<ApplicationContextFactory> factories) {
        // TODO annotation to replace beans, then take care for injectors etc !
        // idea: save th replacement and execute existing injectors
        factories.stream()
                .map(ApplicationContextFactory::getSingletonLoaders)
                .flatMap(Collection::stream)
                .map(SingletonLoaderBase::load)
                .forEach(context::addBean);
    }

    private void doInjection(ApplicationContextImpl context, List<ApplicationContextFactory> factories) {
        factories.stream()
                .map(ApplicationContextFactory::getInjectors)
                .flatMap(Collection::stream)
                .forEach(injector -> injector.doInject(context));
    }

    private void doMultiInjection(ApplicationContextImpl context, List<ApplicationContextFactory> factories) {
        factories.stream()
                .map(ApplicationContextFactory::getMultiInjectors)
                .flatMap(Collection::stream)
                .forEach(injector -> injector.doInject(context));
    }

    private void doPropertyInjection(ApplicationContextImpl context, List<ApplicationContextFactory> factories) {
        factories.stream()
                .map(ApplicationContextFactory::getPropertyInjectors)
                .flatMap(Collection::stream)
                .forEach(injector -> injector.doInject(context));
    }

    private void doInitialize(ApplicationContextImpl context, List<ApplicationContextFactory> factories) {
        factories.stream()
                .map(ApplicationContextFactory::getInitializers)
                .flatMap(Collection::stream)
                .forEach(initializer -> initializer.invokeInit(context));
    }
}
