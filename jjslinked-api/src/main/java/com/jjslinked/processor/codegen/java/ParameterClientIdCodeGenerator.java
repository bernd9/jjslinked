package com.jjslinked.processor.codegen.java;

public class ParameterClientIdCodeGenerator extends JavaCodeGenerator<ParameterClientIdCodeTemplate, ParameterClientIdModel, ParameterClientIdModel> {

    ParameterClientIdCodeGenerator() {
        super(new ParameterClientIdCodeTemplate());
    }

    @Override
    ParameterClientIdModel toRenderModel(ParameterClientIdModel model) {
        return model;
    }
}
