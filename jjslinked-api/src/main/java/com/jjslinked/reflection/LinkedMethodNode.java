package com.jjslinked.reflection;

import com.jjslinked.annotations.LinkedMethod;
import lombok.Builder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class LinkedMethodNode {

    private SimpleTypeNode returnType;
    private String name;
    private boolean implemented;
    private List<ParameterNode> parameterNodes;

    static List<LinkedMethodNode> linkedMethods(TypeElement e) {
        return e.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(LinkedMethodNode::isLinkedMethod)
                .map(LinkedMethodNode::createLinkedMethodNode)
                .collect(Collectors.toList());
    }

    static boolean isLinkedMethod(ExecutableElement e) {
        return e.getAnnotation(LinkedMethod.class) != null;
    }

    static LinkedMethodNode createLinkedMethodNode(ExecutableElement e) {
        return LinkedMethodNode.builder()
                .name(e.getSimpleName().toString())
                .implemented(isImplemented(e))
                .returnType(SimpleTypeNode.create(e.getReturnType()))
                .parameterNodes(ParameterNode.parameterNodes(e))
                .build();

    }

    private static boolean isImplemented(ExecutableElement e) {
        return !e.getModifiers().contains(Modifier.ABSTRACT);
    }
}
