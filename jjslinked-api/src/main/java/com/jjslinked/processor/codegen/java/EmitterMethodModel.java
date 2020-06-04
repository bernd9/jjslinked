package com.jjslinked.processor.codegen.java;

import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EmitterMethodModel implements JavaCodeModel {

    private final String name;
    private final List<ParameterModel> parameters;

    public EmitterMethodModel(ExecutableElement e) {
        this.name = e.getSimpleName().toString();
        this.parameters = e.getParameters().stream().map(ParameterModel::new).collect(Collectors.toList());
    }
}
