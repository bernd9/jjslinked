package com.ejc.processor;


import lombok.Data;
import lombok.Getter;

import javax.lang.model.element.*;
import java.util.*;
import java.util.stream.Collectors;

class SingletonWriterModels {

    @Getter
    private Set<SingletonWriterModel> singletonWriterModels = new HashSet<>();

    SingletonWriterModels(Map<TypeElement, List<TypeElement>> hierarchy) {
        hierarchy.forEach((type, hierarchyList) -> singletonWriterModels.add(new SingletonWriterModel(type, hierarchyList)));
    }

    void putInitMethods(Element type, Collection<ExecutableElement> methods) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getInitMethods().addAll(methods));
    }

    void putBeanMethods(Element type, Collection<ExecutableElement> methods) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getBeanMethods().addAll(methods));
    }

    void putDependencyFields(Element type, Collection<VariableElement> fields) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getDependencyFields().addAll(fields));
    }

    void putCollectionDependencyFields(Element type, Collection<VariableElement> fields) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getCollectionDependencyFields().addAll(fields));
    }

    void putConfigFields(Element type, Collection<VariableElement> fields) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getConfigFields().addAll(fields));
    }

    void putImplementation(Element base, TypeElement impl) {
        singletonWriterModels.stream()
                .filter(model -> model.getSingleton().equals(base))
                .forEach(model -> model.setImplementation(impl));
    }

    void putConstructorParameters(Element type, List<ConstructorParameterElement> parameters) {
        singletonWriterModels.stream()
                .filter(model -> model.getSingleton().equals(type))
                .forEach(model -> model.setConstructorParameters(parameters));
    }

    @Data
    class SingletonWriterModel {

        private final TypeElement singleton;
        private final Set<Name> parentTypes;
        private List<ConstructorParameterElement> constructorParameters;
        private TypeElement implementation;

        private final Set<ExecutableElement> initMethods = new HashSet<>();
        private final Set<ExecutableElement> beanMethods = new HashSet<>();
        private final Set<VariableElement> dependencyFields = new HashSet<>();
        private final Set<VariableElement> collectionDependencyFields = new HashSet<>();
        private final Set<VariableElement> configFields = new HashSet<>();

        SingletonWriterModel(TypeElement singleton, List<TypeElement> parentTypes) {
            this.singleton = singleton;
            this.parentTypes = parentTypes.stream().map(TypeElement::getQualifiedName).collect(Collectors.toSet());
        }

        boolean isAncestorClass(Element type) {
            if (!TypeElement.class.isInstance(type)) return false;
            return parentTypes.contains(((TypeElement) type).getQualifiedName());
        }

    }
}
