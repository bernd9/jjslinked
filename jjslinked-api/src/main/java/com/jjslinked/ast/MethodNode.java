package com.jjslinked.ast;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MethodNode {
    ClassNode declaringClass;
    ClassNode returnType;
    String name;
    List<ParameterNode> parameters;
    Set<AnnotationModel> annotations;

    public Set<String> getAnnotationClasses() {
        return annotations.stream().map(AnnotationModel::getQualifiedName).collect(Collectors.toSet());
    }

    public List<String> getParameterTypes() {
        return parameters.stream()
                .map(ParameterNode::getParameterType)
                .map(ClassNode::getQualifiedName)
                .collect(Collectors.toList());
    }
}
