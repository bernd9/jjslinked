package com.ejc.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

class SingletonElementMapper {

    private Set<SingletonElement> singletonElements = new HashSet<>();

    ModuleFactoryWriterModel getSingletonWriterModel(String applicationClass) {
        return new ModuleFactoryWriterModel(singletonElements, applicationClass);
    }

    SingletonElementMapper(Map<TypeElement, List<TypeElement>> hierarchy) {
        hierarchy.forEach((type, hierarchyList) -> singletonElements.add(new SingletonElement(type, hierarchyList)));
    }

    void putInitMethods(Element type, Collection<ExecutableElement> methods) {
        singletonElements.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getInitMethods().addAll(methods));
    }

    void putBeanMethods(Element type, Collection<ExecutableElement> methods) {
        singletonElements.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getBeanMethods().addAll(methods));
    }

    void putDependencyFields(Element type, Collection<VariableElement> fields) {
        singletonElements.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getDependencyFields().addAll(fields));
    }

    void putCollectionDependencyFields(Element type, Collection<VariableElement> fields) {
        singletonElements.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getCollectionDependencyFields().addAll(fields));
    }

    void putConfigFields(Element type, Collection<VariableElement> fields) {
        singletonElements.stream()
                .filter(model -> model.isAncestorClass(type))
                .forEach(model -> model.getConfigFields().addAll(fields));
    }

    void putImplementation(Element base, TypeElement impl) {
        singletonElements.stream()
                .filter(model -> model.getSingleton().equals(base))
                .forEach(model -> model.setImplementation(impl));
    }

    void putConstructor(Element type, ExecutableElement constructor) {
        singletonElements.stream()
                .filter(model -> model.getSingleton().equals(type))
                .forEach(model -> model.setConstructor(constructor));
    }

}
