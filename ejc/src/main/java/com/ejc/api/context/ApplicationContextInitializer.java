package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContextInitializer {

    private final Collection<SingletonConstructor> singletonConstructors = new HashSet<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final SimpleDependencyInjection simpleDependencyInjection = new SimpleDependencyInjection(this);
    private final BeanMethodInvocation beanMethodInvocation = new BeanMethodInvocation(this);
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
        beanMethodInvocation.addBeanMethods(module.getBeanMethods());
        initInvokers.putAll(module.getInitInvokers());
        collectionDependencyFields.addAll(module.getCollectionDependencyFields().values().stream().flatMap(Collection::stream).collect(Collectors.toSet())); // TODO  remove map in module
        configFields.putAll(module.getConfigFields());
        simpleDependencyInjection.addFields(module.getDependencyFields());
    }

    public void initialize() {
        doReplacement();
        long t0 = System.currentTimeMillis();
        Set<ClassReference> allSingletonTypes = getExpectedSingletonTypes();
        System.out.println("listing all types took " + (System.currentTimeMillis() - t0));
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
            beanMethodInvocation.remove(type);
            initInvokers.remove(type);
            configFields.remove(type);
            simpleDependencyInjection.removeType(type);
            // TODO remove collection-dependency fields, too
        });
    }

    private Set<ClassReference> getExpectedSingletonTypes() {
        return Stream.concat(singletonConstructors.stream(), beanMethodInvocation.getAllBeanMethods())
                .map(SingletonProvider::getSingletonTypes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public void onSingletonCreated(Object o) {
        injectConfigFields(o);
        constructorsOnSingletonCreated(o);
        beanMethodInvocation.onSingletonCreated(o);
        simpleDependencyInjection.onSingletonCreated(o);
        singletons.add(o);
    }


    private void constructorsOnSingletonCreated(Object o) {
        Set<SingletonConstructor> invocableConstructors = singletonConstructors.stream()
                .peek(provider -> provider.onSingletonCreated(o))
                .filter(SingletonProvider::isSatisfied)
                .collect(Collectors.toSet());
        singletonConstructors.removeAll(invocableConstructors);
        invocableConstructors.stream()
                .map(SingletonConstructor::create)
                .forEach(this::onSingletonCreated);
    }

    public void onDependencyFieldsComplete(Object o) {
        invokeInitMethods(o);
        beanMethodInvocation.onDependenciesInjected(o);
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

    static class Xyz {
        Xyz(String s) {

        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Class.forName("com.ejc.api.context.ApplicationContextInitializer.Xyz"));

    }
}


