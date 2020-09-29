package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
public class SingletonModel {
    private final ClassReference type;
    private final Optional<ClassReference> typeToReplace;
    private final SingletonConstructor constructor;
    private Set<BeanMethod> beanMethods = new HashSet<>();
    private Set<InitMethod> initMethods = new HashSet<>();
    private Set<DependencyField> dependencyFields = new HashSet<>();
    private Set<CollectionDependencyField> collectionDependencyFields = new HashSet<>();
    private Set<ConfigValueField> configValueFields = new HashSet<>();
    private Optional<ClassReference> replaceClass;

    SingletonModel(ClassReference type, Optional<ClassReference> typeToReplace) {
        this.type = type;
        this.typeToReplace = typeToReplace;
        constructor = new SingletonConstructor(type);
    }

    public boolean isCreatable() {
        return constructor.allParametersSatisfied();
    }

    public void addConstructorParameter(ClassReference parameterType) {
        constructor.add(new SimpleConstructorParameter(parameterType, constructor));
    }

    public void addConstructorCollectionParameter(Class<? extends Collection> collectionType, ClassReference genericType) {
        constructor.add(new CollectionConstructorParameter(collectionType, genericType));
    }

    public void addBeanMethod(String name) {
        beanMethods.add(new BeanMethod(name));
    }

    public void addInitMethod(String name) {
        initMethods.add(new InitMethod(name));
    }

    public void addConfigValueField(String name, ClassReference fieldType) {
        configValueFields.add(new ConfigValueField(name, fieldType));
    }

    public void addDependencyValueField(String name, ClassReference fieldType) {
        dependencyFields.add(new DependencyField(name, fieldType));
    }

    public void addCollectionDependencyValueField(String name, ClassReference fieldType, ClassReference genericType) {
        collectionDependencyFields.add(new CollectionDependencyField(name, fieldType, genericType));
    }

    public void bindEvents(SingletonCreationEvents bus) {
        constructor.setEvents(bus);
    }

    public void create() {
        try {
            constructor.invoke();
        } catch (Exception e) {
            throw new RuntimeException("unable to create " + type.getClassName());
        }

    }
}
