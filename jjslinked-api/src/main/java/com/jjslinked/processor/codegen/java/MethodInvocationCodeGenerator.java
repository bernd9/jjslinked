package com.jjslinked.processor.codegen.java;

import java.util.List;
import java.util.stream.Collectors;

public class MethodInvocationCodeGenerator extends JavaCodeGenerator<MethodInvocationCodeTemplate, MethodInvocationModel, MethodInvocationRenderModel> {

    private final ParameterClientIdCodeGenerator parameterClientIdCodeGenerator;
    private final ParameterPrimitiveOrWrapperCodeGenerator parameterPrimitiveOrWrapperCodeGenerator;

    MethodInvocationCodeGenerator() {
        super(new MethodInvocationCodeTemplate());
        this.parameterClientIdCodeGenerator = new ParameterClientIdCodeGenerator();
        this.parameterPrimitiveOrWrapperCodeGenerator = new ParameterPrimitiveOrWrapperCodeGenerator();
    }

    @Override
    MethodInvocationRenderModel toRenderModel(MethodInvocationModel model) {
        return new MethodInvocationRenderModel(model, parameterProviders(model));
    }

    private List<String> parameterProviders(MethodInvocationModel model) {
        return model.getParameters().stream().map(this::asString).collect(Collectors.toList());
    }

    private String asString(ParameterCodeModel codeModel) {
        if (codeModel instanceof ParameterClientIdModel) {
            return parameterClientIdCodeGenerator.asString((ParameterClientIdModel) codeModel);
        }
        if (codeModel instanceof DefaultParameterCodeModel) {
            DefaultParameterCodeModel model = (DefaultParameterCodeModel) codeModel;
            if (model.isPrimitive()) {
                return parameterPrimitiveOrWrapperCodeGenerator.asString(model);
            }
            if (model.isPrimitiveWrapper()) {
                return parameterPrimitiveOrWrapperCodeGenerator.asString(model);
            }
            if (model.isCharSequence()) {

            }
            return null;
        }
        throw new IllegalStateException();
    }

}
