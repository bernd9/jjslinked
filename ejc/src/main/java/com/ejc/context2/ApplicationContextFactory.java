package com.ejc.context2;

import com.ejc.ApplicationContext;
import com.ejc.api.context.ClassReference;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextFactory {
    private final Class<?> applicationClass;
    private final SingletonCreationContext context = new SingletonCreationContext();
    private final SingletonProviders singletonProviders = new SingletonProviders();
    private final Set<Object> singletons = new HashSet<>();

    private Map<ClassReference, SingletonObject> singletonObjectMap;
    private Collection<SingletonConstructor> singletonConstructors;

    public void init() {
        ModuleComposer moduleComposer = new ModuleComposer(loadModules(), applicationClass);
        singletonObjectMap = moduleComposer.getSingletonObjectMap();
        singletonConstructors = moduleComposer.getSingletonConstructors();
        singletonProviders.addProviders(singletonConstructors);
        singletonProviders.addProviders(extractBeanMethods(singletonObjectMap.values()));
        context.getSingletonEvents().addListener(singletons::add);
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
        executableConstructors.forEach(SingletonConstructor::invoke);
    }

    private Set<Module> loadModules() {
        ModuleFactoryLoader loader = new ModuleFactoryLoader();
        return loader.load().stream()
                .peek(factory -> factory.setContext(context))
                .peek(ModuleFactory::init)
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
