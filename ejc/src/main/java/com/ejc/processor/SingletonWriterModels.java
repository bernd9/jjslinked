package com.ejc.processor;


import lombok.Data;
import lombok.Getter;

import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class SingletonWriterModels {

    @Getter
    private Set<SingletonWriterModel> singletonWriterModels = new HashSet<>();

    SingletonWriterModels(Map<TypeElement, List<TypeElement>> hierarchy) {
        hierarchy.forEach((type, hierarchyList) -> singletonWriterModels.add(new SingletonWriterModel(type, hierarchyList)));
    }

    void putInitMethod(Element type, ExecutableElement method) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getInitMethods().add(method));
    }

    void putBeanMethod(Element type, ExecutableElement method) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getBeanMethods().add(method));
    }

    void putDependencyField(Element type, VariableElement field) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getDependencyFields().add(field));
    }

    void putConfigField(Element type, VariableElement field) {
        singletonWriterModels.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getConfigFields().add(field));
    }

    void putImplementation(Element base, TypeElement impl) {
        singletonWriterModels.stream()
                .filter(model -> model.getSingleton().equals(base))
                .forEach(model -> model.setImplementation(impl));
    }

    void putConstructor(Element type, ExecutableElement constructor) {
        singletonWriterModels.stream()
                .filter(model -> model.getSingleton().equals(type))
                .forEach(model -> model.setConstructor(constructor));
    }

    @Data
    class SingletonWriterModel {

        private final TypeElement singleton;
        private final Set<Name> parentTypes;
        private ExecutableElement constructor;
        private TypeElement implementation;

        private final Set<ExecutableElement> initMethods = new HashSet<>();
        private final Set<ExecutableElement> beanMethods = new HashSet<>();
        private final Set<VariableElement> dependencyFields = new HashSet<>();
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
