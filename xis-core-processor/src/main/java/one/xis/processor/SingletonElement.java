package one.xis.processor;

import lombok.Data;

import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
class SingletonElement {

    private final TypeElement singleton;
    private final Set<Name> parentTypes;
    private ExecutableElement constructor;
    private TypeElement implementation;

    private final Set<ExecutableElement> initMethods = new HashSet<>();
    private final Set<ExecutableElement> beanMethods = new HashSet<>();
    private final Set<VariableElement> dependencyFields = new HashSet<>();
    private final Set<VariableElement> collectionDependencyFields = new HashSet<>();
    private final Set<VariableElement> configFields = new HashSet<>();
    private final Set<VariableElement> collectionConfigFields = new HashSet<>();
    private final Set<VariableElement> mapConfigFields = new HashSet<>();

    SingletonElement(TypeElement singleton, List<TypeElement> parentTypes) {
        this.singleton = singleton;
        this.parentTypes = parentTypes.stream().map(TypeElement::getQualifiedName).collect(Collectors.toSet());
    }

    boolean isAncestorClass(Element type) {
        if (!TypeElement.class.isInstance(type)) return false;
        return parentTypes.contains(((TypeElement) type).getQualifiedName());
    }

}
