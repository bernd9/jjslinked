package com.jjslinked.model;

import com.jjslinked.processor.util.AnnotationUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MethodModel {
    ClassModel declaringClass;
    String name;
    List<ParameterModel> parameters;
    Set<AnnotationModel> annotations;

    public static MethodModel fromElement(ExecutableElement element) {
        return MethodModel.builder()
                .declaringClass(ClassModel.fromElement((TypeElement) element.getEnclosingElement()))
                .name(element.getSimpleName().toString())
                .parameters(element.getParameters().stream().map(ParameterModel::fromParameter).collect(Collectors.toList()))
                .annotations(AnnotationUtil.getAnnotations(element))
                .build();
    }

    public Set<String> getAnnotationClasses() {
        return annotations.stream().map(AnnotationModel::getQualifiedName).collect(Collectors.toSet());
    }

    public List<String> getParameterTypes() {
        return parameters.stream().map(ParameterModel::getQualifiedName).collect(Collectors.toList());
    }
}
