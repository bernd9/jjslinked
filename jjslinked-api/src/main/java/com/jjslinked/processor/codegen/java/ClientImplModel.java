package com.jjslinked.processor.codegen.java;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientImplModel implements JavaCodeModel {
    private String superClassName;
    private List<MethodInvocationModel> methodInvocations;
    private List<EmitterMethodModel> emitterMethods;
    private String implementationClassName;

    public ClientImplModel(TypeElement e) {
        this.superClassName = e.getQualifiedName().toString();
        this.implementationClassName = superClassName + "Impl";
        this.methodInvocations = methodInvocations(e);
        this.emitterMethods = emitterMethods(e);
    }

    @Override
    public Set<String> getImports() {
        Set<String> set = new HashSet<>();
        set.addAll(methodInvocations.stream().map(MethodInvocationModel::getImports).flatMap(Set::stream).collect(Collectors.toSet()));
        set.addAll(emitterMethods.stream().map(JavaCodeModel::getImports).flatMap(Set::stream).collect(Collectors.toSet()));
        set.add(superClassName);
        return set;
    }

    private List<EmitterMethodModel> emitterMethods(TypeElement e) {
        return e.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(JavaCodeGeneratorUtils::isLinkedMethod)
                .filter(JavaCodeGeneratorUtils::isAbstract)
                .map(EmitterMethodModel::new)
                .collect(Collectors.toList());
    }


    private List<MethodInvocationModel> methodInvocations(TypeElement e) {
        return e.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(JavaCodeGeneratorUtils::isLinkedMethod)
                .filter(JavaCodeGeneratorUtils::isImplemented)
                .map(MethodInvocationModel::new)
                .collect(Collectors.toList());
    }

}
