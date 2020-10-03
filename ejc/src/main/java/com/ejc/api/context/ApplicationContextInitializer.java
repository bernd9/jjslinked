package com.ejc.api.context;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContextInitializer {

    private final Map<ClassReference, SingletonConstructor> singletonConstructors = new HashMap<>();
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final Map<ClassReference, Collection<DependencyField>> dependencyFields = new HashMap<>();
    private final Map<ClassReference, Collection<CollectionDependencyField>> collectionDependencyFields = new HashMap<>();
    private final Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();

    private Set<Object> singletons = new HashSet<>();

    @Getter
    private static ApplicationContextInitializer instance;

    public ApplicationContextInitializer() {
        instance = this;
    }

    public void addModule(Module module) {
        singletonConstructors.putAll(module.getSingletonConstructors());
        beanMethods.putAll(module.getBeanMethods());
        initInvokers.putAll(module.getInitInvokers());
        dependencyFields.putAll(module.getDependencyFields());
        collectionDependencyFields.putAll(module.getCollectionDependencyFields());
        configFields.putAll(module.getConfigFields());
    }

    public void initialize() {
        doReplacement();
        Set<ClassReference> allSingletonTypes = getExpectedSingletonTypes();
        publishExpectedSingletonTypes(allSingletonTypes);
        runInstantiation();
    }


    private void runInstantiation() {
        Collection<SingletonConstructor> invokeConstructors = singletonConstructors.values().stream()
                .filter(SingletonProvider::isSatisfied)
                .collect(Collectors.toSet());
        invokeConstructors.stream()
                .map(SingletonConstructor::invoke)
                .forEach(o -> onSingletonCreated(o));
    }

    private void publishExpectedSingletonTypes(Set<ClassReference> allSingletonTypes) {
        singletonConstructors.values().forEach(provider -> provider.registerSingletonTypes(allSingletonTypes));
        collectionDependencyFields.values().stream().flatMap(Collection::stream).forEach(field -> field.registerSingletonTypes(allSingletonTypes));
    }

    private void doReplacement() {
        classesToReplace.forEach(type -> {
            beanMethods.remove(type);
            initInvokers.remove(type);
            dependencyFields.remove(type);
            configFields.remove(type);
        });
    }

    private Set<ClassReference> getExpectedSingletonTypes() {
        return Stream.concat(singletonConstructors.values().stream(), beanMethods.values().stream().flatMap(Collection::stream))
                .map(SingletonProvider::getSingletonTypes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public void onSingletonCreated(Object o) {
        injectConfigFields(o);
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
        constructorsOnSingletonCreated(o);
        dependencyFieldsOnSingletonCreated(o);
        singletons.add(o);
    }


    private void constructorsOnSingletonCreated(Object o) {
        new HashSet<>(singletonConstructors.values())
                .forEach(provider -> provider.onSingletonCreated(o));
    }


    private void dependencyFieldsOnSingletonCreated(Object o) {
        new HashSet<>(dependencyFields.values()).stream()
                .flatMap(Collection::stream)
                .forEach(field -> field.onSingletonCreated(o));
        new HashSet<>(collectionDependencyFields.values()).stream()
                .flatMap(Collection::stream)
                .forEach(field -> field.onSingletonCreated(o));
    }

    public void onDependencyFieldComplete(Object o) {
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
    }

    private void invokeBeanMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        Collection<BeanMethod> invokeMethods = new HashSet<>(beanMethods.getOrDefault(reference, Collections.emptySet()));
        invokeMethods.stream().map(BeanMethod::invoke).forEach(this::onSingletonCreated);
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

    public void remove(SingletonProvider provider) {
        if (provider instanceof SingletonConstructor) {
            singletonConstructors.remove(provider.getType());
        } else if (provider instanceof BeanMethod) {
            Collection<BeanMethod> methods = beanMethods.get(provider.getType());
            methods.remove(provider);
        }
    }

    public void remove(DependencyField field) {
        Collection<DependencyField> fields = dependencyFields.get(field.getDeclaringType());
        fields.remove(field);
    }

    public void remove(CollectionDependencyField field) {
        collectionDependencyFields.get(field.getDeclaringType()).remove(field);
    }

}


