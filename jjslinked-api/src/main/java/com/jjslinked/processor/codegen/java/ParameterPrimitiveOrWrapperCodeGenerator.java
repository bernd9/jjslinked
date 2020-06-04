package com.jjslinked.processor.codegen.java;

public class ParameterPrimitiveOrWrapperCodeGenerator extends JavaCodeGenerator<ParameterPrimitiveOrWrapperCodeTemplate, DefaultParameterCodeModel, DefaultParameterCodeModel> {

    ParameterPrimitiveOrWrapperCodeGenerator() {
        super(new ParameterPrimitiveOrWrapperCodeTemplate());
    }

    @Override
    DefaultParameterCodeModel toRenderModel(DefaultParameterCodeModel model) {
        return model;
    }
}
