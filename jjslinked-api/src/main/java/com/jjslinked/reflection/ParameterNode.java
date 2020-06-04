package com.jjslinked.reflection;

import lombok.Builder;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jjslinked.reflection.ReflectionUtils.packageName;
import static com.jjslinked.reflection.ReflectionUtils.simpleName;

@Builder
public class ParameterNode {

    private Set<String> annotations;
    private String packageName;
    private String simpleName;
    private TypeKind typeKind;

    public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
        return annotations.contains(annotation.getName());
    }

    static List<ParameterNode> parameterNodes(ExecutableElement e) {
        return e.getParameters().stream().map(ParameterNode::createParameterNode).collect(Collectors.toList());
    }

    private static ParameterNode createParameterNode(VariableElement e) {
        String qualifiedName = e.asType().toString();
        return ParameterNode.builder()
                .packageName(packageName(qualifiedName))
                .simpleName(simpleName(qualifiedName))
                .annotations(annotations(e))
                .build();
    }

    private static Set<String> annotations(VariableElement e) {
        return e.getAnnotationMirrors().stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .map(Element::asType)
                .map(TypeMirror::toString)
                .collect(Collectors.toSet());
    }
}
