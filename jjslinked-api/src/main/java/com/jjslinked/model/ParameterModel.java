package com.jjslinked.model;

import com.jjslinked.processor.util.AnnotationUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.lang.model.element.VariableElement;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ParameterModel {
    String parameterName;
    ClassModel parameterType;
    Set<AnnotationModel> annotations;

    public static ParameterModel fromParameter(VariableElement e) {
        return ParameterModel.builder()
                .parameterName(e.getSimpleName().toString())
                .parameterType(ClassModel.from(e))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }

    public Set<String> getAnnotationClasses() {
        return annotations.stream()
                .map(AnnotationModel::getQualifiedName)
                .collect(Collectors.toSet());
    }

}
