package com.jjslinked.processor.codegen.java;


import lombok.Getter;

import javax.lang.model.element.VariableElement;

@Getter
public class ParameterClientIdModel implements ParameterCodeModel {

    private final String providerClassName;

    ParameterClientIdModel(VariableElement e) {
        this.providerClassName = JavaCodeGeneratorUtils.providerClassName(e);
    }
}
