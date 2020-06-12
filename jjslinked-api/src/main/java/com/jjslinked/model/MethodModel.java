package com.jjslinked.model;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class MethodModel {
    private ClassModel declaringType;
    private String name;
    private List<ParameterModel> parameters;
    private Set<ClassModel> annotations;

    public static MethodModel fromElement(ExecutableElement element) {
        return MethodModel.builder()
                .declaringType(ClassModel.fromElement((TypeElement) element.getEnclosingElement()))
                .name(element.getSimpleName().toString())
                .parameters(element.getParameters().stream().map(ParameterModel::fromParameter).collect(Collectors.toList()))
                .build();
    }
}
