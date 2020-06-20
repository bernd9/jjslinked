package com.jjslinked.ast;

import com.jjslinked.processor.util.AnnotationUtil;

import javax.lang.model.element.VariableElement;

public class ParameterNodeBuilder {
    public static ParameterNode of(VariableElement e) {
        return ParameterNode.builder()
                .parameterName(e.getSimpleName().toString())
                .parameterType(ClassNodeBuilder.from(e))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }
}
