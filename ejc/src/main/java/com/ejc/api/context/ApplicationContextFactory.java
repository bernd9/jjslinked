package com.ejc.api.context;

import com.ejc.util.ClassUtils;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextFactory {
    private final SingletonProviders singletonProviders = new SingletonProviders();
    private final Set<Object> singletons = new HashSet<>();

    private Map<ClassReference, SingletonObject> singletonObjectMap;
    private Collection<SingletonConstructor> singletonConstructors;
    private UniqueBeanValidator uniqueBeanValidator;
    private Set<SingletonObject> singletonObjects;
    private ApplicationContextImpl applicationContext = new ApplicationContextImpl();
    private SingletonProcessor singletonProcessor;

    public ApplicationContextFactory(Class<?> applicationClass) {
        init(applicationClass, new ModuleComposer(loadModules(), applicationClass));
    }

    public ApplicationContextFactory(Class<?> applicationClass, Module module) {
        init(applicationClass, new ModuleComposer(Collections.singleton(module), applicationClass));
    }

    private void init(Class<?> applicationClass, ModuleComposer moduleComposer) {
        moduleComposer.composeModules();
        singletonProcessor = new CompoundSingletonProcessor(SingletonProcessorLoader.load());
        singletonObjectMap = moduleComposer.getSingletonObjectMap();
        singletonConstructors = moduleComposer.getSingletonConstructors();
        uniqueBeanValidator = new UniqueBeanValidator(singletonProviders, extractSimpleDependencyFields(singletonObjectMap.values()));
        singletonProviders.addProviders(singletonConstructors);
        singletonProviders.addProviders(extractBeanMethods(singletonObjectMap.values()));
        singletonProviders.addProvider(new SimpleSingletonProvider(applicationContext));
        singletonProviders.addProvider(new SimpleSingletonProvider(ClassUtils.createInstance(applicationClass)));
        singletonObjects = new HashSet<>(singletonObjectMap.values());
    }

    public ApplicationContext createApplicationContext() {
        runInstantiation();
        applicationContext.setBeans(singletons);
        ApplicationContext.instance = applicationContext;
        return applicationContext;
    }


    private void runInstantiation() {
        while (!singletonProviders.getProviders().isEmpty()) {
            Set<SingletonProvider> invocableProviders = singletonProviders.getProviders().stream()
                    .filter(provider -> provider.isSatisfied(singletonProviders))
                    .collect(Collectors.toSet());
            if (invocableProviders.isEmpty()) {
                // TODO better Exception. Add reason to exception text
                throw new IllegalStateException();

            }
            invocableProviders.forEach(this::invokeProviderOrProcessor);
        }
    }

    private void invokeProviderOrProcessor(SingletonProvider provider) {
        singletonProviders.remove(provider);
        singletonProcessor.beforeInstantiation(provider.getType().getReferencedClass())
                .ifPresentOrElse(this::onSingletonCreated, () -> invokeProvider(provider));
    }

    private void invokeProvider(SingletonProvider provider) {
        onSingletonCreated(provider.provide());
    }

    private void onSingletonCreated(Object o) {
        final Object singleton = singletonProcessor.afterInstantiation(o).orElse(o);
        uniqueBeanValidator.onSingletonCreated(o);
        singletonProviders.onSingletonCreated(o);
        singletonObjects.forEach(singletonObject -> singletonObject.onSingletonCreated(singleton, singletonProviders));
        singletons.add(o);
    }

    private Set<Module> loadModules() {
        ModuleFactoryLoader loader = new ModuleFactoryLoader();
        return loader.load().stream()
                .map(ModuleFactory::getModule)
                .collect(Collectors.toSet());
    }

    private static Set<BeanMethod> extractBeanMethods(Collection<SingletonObject> singletonObjects) {
        return singletonObjects.stream()
                .map(SingletonObject::getBeanMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static Set<SimpleDependencyField> extractSimpleDependencyFields(Collection<SingletonObject> singletonObjects) {
        return singletonObjects.stream()
                .map(SingletonObject::getSimpleDependencyFields)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
