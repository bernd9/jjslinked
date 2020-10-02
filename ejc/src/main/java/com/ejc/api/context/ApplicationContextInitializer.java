package com.ejc.api.context;

import com.ejc.api.context.model.ConfigValueField;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextInitializer {

    private final Map<ClassReference, SingletonConstructor> singletonConstructors = new HashMap<>();
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final Map<ClassReference, Collection<DependencyField>> dependencyFields = new HashMap<>();
    private final Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();

    private Set<Object> singletons = new HashSet<>();

    public void addModule(Module module) {
        singletonConstructors.putAll(module.getSingletonConstructors());
        beanMethods.putAll(module.getBeanMethods());
        initInvokers.putAll(module.getInitInvokers());
        dependencyFields.putAll(module.getDependencyFields());
        configFields.putAll(module.getConfigFields());
    }

    public void initialize() {
        classesToReplace.forEach(type -> {
            beanMethods.remove(type);
            initInvokers.remove(type);
            dependencyFields.remove(type);
            configFields.remove(type);
        });

        Set<Class<?>> allSingletonTypes = singletonConstructors.values().stream()
                .map(SingletonProvider::getSingletonTypes)
                .flatMap(Set::stream)
                .map(ClassReference::getReferencedClass)
                .collect(Collectors.toSet());
        singletonConstructors.values().forEach(provider -> provider.setAllSingletonTypes(allSingletonTypes));
    }

    public void onSingletonCreated(Object o) {
        injectConfigFields(o);
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
        singletonConstructors.values().forEach(provider -> provider.onSingletonCreated(o));
        dependencyFields.values().stream()
                .flatMap(Collection::stream)
                .forEach(field -> field.onSingletonCreated(o));
        singletons.add(o);
    }

    public void onDependencyFieldComplete(Object o) {
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
    }

    private void invokeBeanMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        beanMethods.getOrDefault(reference, Collections.emptySet()).stream()
                .map(BeanMethod::create)
                .forEach(this::onSingletonCreated);
    }

    private void invokeInitMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        initInvokers.getOrDefault(reference, Collections.emptySet())
                .forEach(invoker -> invoker.doInvokeMethod(o));
    }

    private void injectConfigFields(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        configFields.getOrDefault(reference, Collections.emptySet())
                .forEach(field -> field.injectConfigValue(o));
    }

    private boolean dependencyFieldsComplete(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        return dependencyFields.getOrDefault(reference, Collections.emptySet())
                .stream().noneMatch(field -> !field.isSatisfied());
    }


}


