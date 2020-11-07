package com.ejc.context2;

import com.ejc.util.CollectorUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class ModuleComposer {
    private final Collection<Module> modules;
    private final Class<?> applicationClass;
    private Module currentModule;
    private Set<Module> loadedModules = new HashSet<>();

    @Getter
    private Map<ClassReference, SingletonObject> singletonObjectMap;

    @Getter
    private Collection<SingletonConstructor> singletonConstructors;

    void composeModules() {
        separateCurrentModule();
        singletonObjectMap = composeSingletonObjects();
        singletonConstructors = cleanedSingletonConstructors(singletonObjectMap.keySet());
    }

    private void separateCurrentModule() {
        currentModule = modules.stream()
                .filter(module -> module.getApplicationClass().equals(applicationClass))
                .collect(CollectorUtils.toOnlyElement());
        loadedModules.addAll(modules);
        loadedModules.remove(currentModule);
    }


    private Map<ClassReference, SingletonObject> composeSingletonObjects() {
        Map<ClassReference, SingletonObject> singletons = new HashMap<>();
        loadedModules.stream()
                .map(Module::getSingletonObjects)
                .forEach(singletons::putAll);
        Set<ClassReference> classesToReplace = loadedModules.stream()
                .map(Module::getClassesToReplace)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        classesToReplace.addAll(currentModule.getClassesToReplace());
        Set<ClassReference> checkReplacement = new HashSet<>(singletons.keySet());
        checkReplacement.removeAll(classesToReplace);
        if (!checkReplacement.isEmpty()) {
            // TODO throw exception
        }
        classesToReplace.forEach(type -> singletons.remove(type));
        return singletons;
    }

    private Collection<SingletonConstructor> cleanedSingletonConstructors(Collection<ClassReference> singletonTypes) {
        return loadedModules.stream()
                .map(Module::getSingletonConstructors)
                .flatMap(Collection::stream)
                .filter(constructor -> singletonTypes.contains(constructor.getType()))
                .collect(Collectors.toSet());
    }

}
