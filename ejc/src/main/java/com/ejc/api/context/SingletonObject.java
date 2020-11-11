package com.ejc.api.context;

import lombok.Data;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Data
class SingletonObject implements SingletonCreationListener {

    private final ClassReference type;
    private final Collection<InitMethod> initMethods = new HashSet<>();
    private final Collection<ConfigField> configFields = new HashSet<>();
    private final Collection<BeanMethod> beanMethods = new HashSet<>();
    private final Collection<SimpleDependencyField> simpleDependencyFields = new HashSet<>();
    private final Collection<CollectionDependencyField> collectionDependencyFields = new HashSet<>();

    private SingletonProviders singletonProviders;
    private Object singleton;

    @Getter
    private boolean disabled;

    @Override
    public void onSingletonCreated(Object o, SingletonEvents events) {
        if (disabled) {
            return;
        }
        if (type.isInstance(o)) {
            if (singleton != null) {
                throw new IllegalStateException();
            }
            singleton = o;
            configFields.forEach(field -> field.injectConfigValue(singleton));
        }
        beanMethods.forEach(method -> method.onSingletonCreated(o));
        simpleDependencyFields.forEach(field -> field.onSingletonCreated(o));
        collectionDependencyFields.forEach(method -> method.onSingletonCreated(o));
        if (singleton != null) {
            singletonCreatedPostAction(events);
        }
    }

    private void singletonCreatedPostAction(SingletonEvents events) {
        setSimpleDependencies();
        setCollectionDependencies();
        if (dependenciesSet()) {
            this.disabled = true;
            invokeInitMethods();
            invokeBeanMethods(events);
        }
    }

    private void invokeInitMethods() {
        initMethods.forEach(method -> method.doInvokeMethod(singleton));
        initMethods.clear();
    }

    private void invokeBeanMethods(SingletonEvents singletonEvents) {
        Collection<BeanMethod> executableBeanMethods = beanMethods.stream()
                .filter(beanMethod -> beanMethod.isSatisfied(singletonProviders))
                .collect(Collectors.toSet());
        beanMethods.removeAll(executableBeanMethods);
        executableBeanMethods.stream()
                .map(method -> method.invoke(singleton))
                .forEach(singletonEvents::onSingletonCreated);
    }


    private void setSimpleDependencies() {
        Collection<SimpleDependencyField> satisfiedFields = simpleDependencyFields.stream()
                .filter(SimpleDependencyField::isSatisfied)
                .collect(Collectors.toSet());
        simpleDependencyFields.removeAll(satisfiedFields);
        satisfiedFields.forEach(field -> field.setFieldValue(singleton));
    }

    private void setCollectionDependencies() {
        Collection<CollectionDependencyField> satisfiedFields = collectionDependencyFields.stream()
                .filter(field -> field.isSatisfied(singletonProviders))
                .collect(Collectors.toSet());
        collectionDependencyFields.removeAll(satisfiedFields);
        satisfiedFields.forEach(field -> field.setFieldValue(singleton));

    }

    private boolean dependenciesSet() {
        return simpleDependencyFields.isEmpty() && collectionDependencyFields.isEmpty();
    }

    public void addConfigField(ConfigField configField) {
        configFields.add(configField);
    }

    public void addInitMethod(InitMethod initMethod) {
        initMethods.add(initMethod);
    }

    public void addBeanMethod(BeanMethod beanMethod) {
        beanMethods.add(beanMethod);
    }

    public void addSimpleDependencyField(SimpleDependencyField simpleDependencyField) {
        simpleDependencyFields.add(simpleDependencyField);
    }

    public void addCollectionDependencyField(CollectionDependencyField collectionDependencyField) {
        collectionDependencyFields.add(collectionDependencyField);
    }
}