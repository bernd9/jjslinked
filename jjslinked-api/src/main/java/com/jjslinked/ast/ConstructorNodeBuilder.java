package com.jjslinked.ast;

import com.jjslinked.processor.util.AnnotationUtil;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstructorNodeBuilder {

    public static Set<ConstructorNode> of(TypeElement e) {
        return e.getEnclosedElements().stream().filter(executables -> e.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .map(ConstructorNodeBuilder::of)
                .collect(Collectors.toSet());
    }

    public static ConstructorNode of(ExecutableElement element) {
        return ConstructorNode.builder()
                .declaringClass(ClassNodeBuilder.from((TypeElement) element.getEnclosingElement()))
                .name(element.getSimpleName().toString())
                .parameters(element.getParameters().stream().map(ParameterNodeBuilder::of).collect(Collectors.toList()))
                .annotations(AnnotationUtil.getAnnotations(element))
                .build();
    }
}
