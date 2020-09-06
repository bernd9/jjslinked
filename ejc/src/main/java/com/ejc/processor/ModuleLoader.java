package com.ejc.processor;

import com.ejc.ApplicationContextFactory;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleLoader {
    private final ApplicationContextFactoryBase actualContextFactory;

    public ModuleLoader(ApplicationContextFactory actualContextFactory) {
        this.actualContextFactory = (ApplicationContextFactoryBase) actualContextFactory;
    }

    public void addModules() {
        Set<ApplicationContextFactory> factories = loadContext();
        doClassReplacement(factories);
        appendFactories(factories);
    }

    private Set<ApplicationContextFactory> loadContext() {
        ServiceLoader<ApplicationContextFactory> loader = ServiceLoader.load(ApplicationContextFactory.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }

    private void doClassReplacement(Collection<ApplicationContextFactory> factories) {
        factories.stream()
                .map(ApplicationContextFactoryBase.class::cast)
                .forEach(factoryInJar -> factoryInJar.removeBeanClasses(actualContextFactory.getClassesToReplace()));
    }

    private void appendFactories(Collection<ApplicationContextFactory> factories) {
        factories.stream().forEach(actualContextFactory::append);
    }
}
