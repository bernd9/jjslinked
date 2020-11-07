package com.ejc.api.context;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextFactory {
    private final Class<?> applicationClass;
    private final SingletonEvents singletonEvents = new SingletonEvents();
    private final SingletonProviders singletonProviders = new SingletonProviders();
    private final Set<Object> singletons = new HashSet<>();

    private Map<ClassReference, SingletonObject> singletonObjectMap;
    private Collection<SingletonConstructor> singletonConstructors;

    public void init() {
        ModuleComposer moduleComposer = new ModuleComposer(loadModules(), applicationClass);
        moduleComposer.composeModules();
        singletonObjectMap = moduleComposer.getSingletonObjectMap();
        singletonConstructors = moduleComposer.getSingletonConstructors();
        singletonProviders.addProviders(singletonConstructors);
        singletonProviders.addProviders(extractBeanMethods(singletonObjectMap.values()));
        singletonEvents.addSingletonCreationListener((singleton, events) -> singletons.add(singleton));
        singletonConstructors.forEach(singletonEvents::addSingletonCreationListener);
        singletonObjectMap.values().forEach(singletonEvents::addSingletonCreationListener);
    }

    public ApplicationContext createApplicationContext() {
        startInstantiation();
        ApplicationContext applicationContext = new ApplicationContextImpl(singletons);
        singletons.add(applicationContext);
        return applicationContext;
    }

    private void startInstantiation() {
        Collection<SingletonConstructor> executableConstructors = singletonConstructors.stream()
                .filter(singletonConstructor -> singletonConstructor.isSatisfied(singletonProviders))
                .collect(Collectors.toSet());
        if (executableConstructors.isEmpty()) {
            // TODO throw exception. Because Configuration-Constructors are included, there must be at least one executable.
        }
        singletonConstructors.removeAll(executableConstructors);
        executableConstructors.forEach(constructor -> constructor.invoke(singletonEvents));
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

}
