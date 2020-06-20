package com.jjslinked.ast;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ParameterNode {
    String parameterName;
    ClassNode parameterType;
    Set<AnnotationModel> annotations;

    public Set<String> getAnnotationClasses() {
        return annotations.stream()
                .map(AnnotationModel::getQualifiedName)
                .collect(Collectors.toSet());
    }

}
