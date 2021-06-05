package com.ejc.api.context;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Data
class SingletonObject {

    private final ClassReference type;
    private final Collection<InitMethod> initMethods = new HashSet<>();
    private final Collection<ConfigField> configFields = new HashSet<>();
    private final Collection<BeanMethod> beanMethods = new HashSet<>();
    private final Collection<SimpleDependencyField> simpleDependencyFields = new HashSet<>();
    private final Collection<CollectionDependencyField> collectionDependencyFields = new HashSet<>();

    private Object singleton;
    private boolean satisfied;

    void onSingletonCreated(Object o, SingletonProviders singletonProviders) {
        if (!satisfied) {
            simpleDependencyFields.forEach(field -> field.onSingletonCreated(o));
            collectionDependencyFields.forEach(field -> field.onSingletonCreated(o));
            if (type.equalClass(o)) {
                if (singleton != null) {
                    throw new IllegalStateException(); // TODO special exception, more info
                }
                singleton = o;
                configFields.forEach(field -> field.injectConfigValue(singleton));
                collectionDependencyFields.forEach(field -> field.setFieldValue(singleton));
            }
            if (singleton != null) {
                setSimpleDependencies(singleton);
                clearSatisfiedCollectionDependencies(singletonProviders);
                if (dependenciesSet()) {
                    invokeInitMethods();
                    satisfied = true;
                }
            }
        }
    }

    private void invokeInitMethods() {
        initMethods.forEach(method -> method.doInvokeMethod(singleton));
        initMethods.clear();
    }

    private void setSimpleDependencies(Object singleton) {
        Collection<SimpleDependencyField> satisfiedFields = simpleDependencyFields.stream()
                .filter(SimpleDependencyField::isSatisfied)
                .collect(Collectors.toSet());
        simpleDependencyFields.removeAll(satisfiedFields);
        satisfiedFields.forEach(field -> field.setFieldValue(singleton));
    }

    private void clearSatisfiedCollectionDependencies(SingletonProviders singletonProviders) {
        Collection<CollectionDependencyField> satisfiedFields = collectionDependencyFields.stream()
                .filter(field -> field.isSatisfied(singletonProviders))
                .collect(Collectors.toSet());
        collectionDependencyFields.removeAll(satisfiedFields);
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
