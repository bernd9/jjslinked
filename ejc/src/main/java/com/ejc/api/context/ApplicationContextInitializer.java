package com.ejc.api.context;

import lombok.Getter;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContextInitializer {

    private final Collection<SingletonConstructor> singletonConstructors = new HashSet<>();
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final SimpleDependencyInjection simpleDependencyInjection = new SimpleDependencyInjection(this);
    private final Collection<CollectionDependencyField> collectionDependencyFields = new HashSet<>();
    private final Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();

    @Getter
    private Set<Object> singletons = new HashSet<>();

    @Getter
    private static ApplicationContextInitializer instance;

    public ApplicationContextInitializer() {
        instance = this;
    }

    public void addModule(Module module) {
        singletonConstructors.addAll(module.getSingletonConstructors());
        beanMethods.putAll(module.getBeanMethods());
        initInvokers.putAll(module.getInitInvokers());
        collectionDependencyFields.addAll(module.getCollectionDependencyFields().values().stream().flatMap(Collection::stream).collect(Collectors.toSet())); // TODO  remove map in module
        configFields.putAll(module.getConfigFields());
        simpleDependencyInjection.addFields(module.getDependencyFields());

    }

    public void initialize() {
        doReplacement();
        Set<ClassReference> allSingletonTypes = getExpectedSingletonTypes();
        publishExpectedSingletonTypes(allSingletonTypes);
        runConstructorInstantiation();
    }


    private void runConstructorInstantiation() {
        Set<SingletonConstructor> executableConstructors = singletonConstructors.stream()
                .filter(SingletonProvider::isSatisfied)
                .collect(Collectors.toSet());
        singletonConstructors.removeAll(executableConstructors);
        executableConstructors.stream().map(SingletonConstructor::create)
                .forEach(this::onSingletonCreated);
    }

    private void publishExpectedSingletonTypes(Set<ClassReference> allSingletonTypes) {
        singletonConstructors.forEach(provider -> provider.registerSingletonTypes(allSingletonTypes));
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
        return Stream.concat(singletonConstructors.stream(), beanMethods.values().stream().flatMap(Collection::stream))
                .map(SingletonProvider::getSingletonTypes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public void onSingletonCreated(Object o) {
        injectConfigFields(o);
        constructorsOnSingletonCreated(o);
        simpleDependencyInjection.onSingletonCreated(o);
        singletons.add(o);
    }


    private void constructorsOnSingletonCreated(Object o) {
        singletonConstructors.stream()
                .peek(provider -> provider.onSingletonCreated(o))
                .filter(SingletonProvider::isSatisfied)
                .map(SingletonConstructor::create)
                .forEach(this::onSingletonCreated);
    }


    /*
    private Map<ClassReference, Set<SimpleDependencyField>> ownerCache = new HashMap<>();

    private Set<SimpleDependencyField> getByOwnerType(ClassReference reference) {
        return ownerCache.computeIfAbsent(reference, type -> dependencyFieldsByOwner.stream()
                .filter(field -> field.getDeclaringType().equals(reference))
                .collect(Collectors.toSet()));
    }


    private Map<ClassReference, Set<SimpleDependencyField>> fieldTypeCache = new HashMap<>();

    private Set<SimpleDependencyField> getByFieldTypeType(ClassReference reference) {
        return fieldTypeCache.computeIfAbsent(reference, type -> dependencyFieldsByOwner.stream()
                .filter(field -> field.getFieldType().equals(reference))
                .collect(Collectors.toSet()));
    }

    */


    public void onDependencyFieldsComplete(Object o) {
        invokeInitMethods(o);
        invokeBeanMethods(o);

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

    public static void main(String[] args) {
        Integer i = 1;
        List<WeakReference<Integer>> list = new ArrayList<>();
        list.add(new WeakReference<>(i));
        i = null;
        System.out.println(list.get(0).get());

    }
}


