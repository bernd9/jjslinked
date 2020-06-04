package com.jjslinked.processor.codegen.java;

import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.List;

public class MethodInvocationRenderModel {

    @Delegate
    private final MethodInvocationModel methodInvocationModel;

    @Getter
    private List<String> parameterProviders;

    public MethodInvocationRenderModel(MethodInvocationModel methodInvocationModel, List<String> parameterProviders) {
        this.methodInvocationModel = methodInvocationModel;
        this.parameterProviders = parameterProviders;
    }
}
