package com.ejc.api.context;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContextInitializer {

    private final Map<ClassReference, SingletonConstructor> singletonConstructors = new HashMap<>();
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final Collection<SimpleDependencyField> dependencyFields = new HashSet<>();
    private final Collection<CollectionDependencyField> collectionDependencyFields = new HashSet<>();
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
        dependencyFields.addAll(module.getDependencyFields().values().stream().flatMap(Collection::stream).collect(Collectors.toSet())); // TODO  remove map in module
        collectionDependencyFields.addAll(module.getCollectionDependencyFields().values().stream().flatMap(Collection::stream).collect(Collectors.toSet())); // TODO  remove map in module
        configFields.putAll(module.getConfigFields());
    }

    public void initialize() {
        doReplacement();
        Set<ClassReference> allSingletonTypes = getExpectedSingletonTypes();
        publishExpectedSingletonTypes(allSingletonTypes);
        runConstructorInstantiation();
    }


    private void runConstructorInstantiation() {
        Set<ClassReference> executableConstructorTypes = singletonConstructors.entrySet().stream()
                .filter(e -> e.getValue().isSatisfied())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        executableConstructorTypes.stream()
                .map(type -> singletonConstructors.remove(type))
                .filter(Objects::nonNull)
                .map(SingletonProvider::invoke)
                .forEach(this::onSingletonCreated);
    }

    private void publishExpectedSingletonTypes(Set<ClassReference> allSingletonTypes) {
        singletonConstructors.values().forEach(provider -> provider.registerSingletonTypes(allSingletonTypes));
        collectionDependencyFields.forEach(field -> field.registerSingletonTypes(allSingletonTypes));
    }

    private void doReplacement() {
        classesToReplace.forEach(type -> {
            beanMethods.remove(type);
            initInvokers.remove(type);
            configFields.remove(type);
            // TODO remove dependency fields, too
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
        singletonConstructors.values().forEach(provider -> provider.onSingletonCreated(o));
        runConstructorInstantiation();
    }


    private void dependencyFieldsOnSingletonCreated(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        dependencyFields.stream().filter(field -> field.getDeclaringType().equals(reference))
                .forEach(field -> field.setOwner(o));
        dependencyFields.stream().filter(field -> field.getFieldType().equals(reference))
                .forEach(field -> field.setFieldValue(o));
        Set<SimpleDependencyField> satisfied = dependencyFields.stream()
                .filter(SimpleDependencyField::isSatisfied)
                .peek(field -> field.setFieldValue())
                .collect(Collectors.toSet());
        dependencyFields.removeAll(satisfied);


    }

    public void onDependencyFieldComplete(Object o) {
        // TODO
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
    }

    private void invokeBeanMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        Collection<BeanMethod> invokeMethods = beanMethods.getOrDefault(reference, Collections.emptySet())
                .stream().filter(BeanMethod::isSatisfied).collect(Collectors.toSet());
        beanMethods.getOrDefault(reference, Collections.emptySet()).removeAll(invokeMethods);
        invokeMethods.stream().map(BeanMethod::invoke).forEach(this::onSingletonCreated);
    }

    private void invokeInitMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        Collection<InitMethodInvoker> invokers = initInvokers.remove(reference);
        if (invokers != null)
            invokers.forEach(invoker -> invoker.doInvokeMethod(o));
    }

    private void injectConfigFields(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        Collection<ConfigValueField> fields = configFields.remove(reference);
        if (fields != null)
            fields.forEach(field -> field.injectConfigValue(o));
    }

    private boolean dependencyFieldsComplete(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        return dependencyFields.stream().filter(field -> field.getDeclaringType().equals(reference)).count() == 0;
    }
}


