package one.xis.context;

import com.ejc.util.CollectionUtils;
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
        Map<ClassReference, ClassReference> replacements = composeClassReplacements();
        doReplace(singletonObjectMap, replacements);
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
        singletons.putAll(currentModule.getSingletonObjects());
        return singletons;
    }


    private Map<ClassReference, ClassReference> composeClassReplacements() {
        Map<ClassReference, ClassReference> classReplacements = new HashMap<>();
        loadedModules.forEach(module -> classReplacements.putAll(module.getClassReplacements()));
        classReplacements.putAll(currentModule.getClassReplacements());
        return classReplacements;
    }

    private static void doReplace(Map<ClassReference, SingletonObject> singletons, Map<ClassReference, ClassReference> replacements) {
        while (!replacements.isEmpty()) {
            Map.Entry<ClassReference, ClassReference> replacement = CollectionUtils.getFirstOrThrow(replacements.entrySet());
            if (singletons.containsKey(replacement.getKey())) {
                SingletonObject replacingObject = singletons.get(replacement.getValue());
                if (replacingObject == null) {
                    throw new IllegalStateException();
                }
                replacements.remove(replacement.getKey());
                singletons.remove(replacement.getKey());
                singletons.put(replacement.getValue(), replacingObject);
            } else {
                if (!replacements.values().contains(replacement.getKey())) {
                    throw new IllegalStateException(replacement.getKey().getClassName()
                            + " can not be replaced by "
                            + replacement.getValue().getClassName()
                            + " because it does not exist");
                }
            }
        }
    }

    private Collection<SingletonConstructor> cleanedSingletonConstructors(Collection<ClassReference> singletonTypes) {
        Collection<SingletonConstructor> constructors = new HashSet<>();
        constructors.addAll(currentModule.getSingletonConstructors());
        constructors.addAll(loadedModules.stream()
                .map(Module::getSingletonConstructors)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return constructors.stream().filter(constructor -> singletonTypes.contains(constructor.getType()))
                .collect(Collectors.toSet());
    }

}
