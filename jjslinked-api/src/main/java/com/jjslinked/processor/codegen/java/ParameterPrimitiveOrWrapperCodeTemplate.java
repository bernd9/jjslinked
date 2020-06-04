package com.jjslinked.processor.codegen.java;

import com.jjslinked.model.ClientMessage;
import com.jjslinked.validation.ValidationExecutor;

import java.util.Set;

public class ParameterPrimitiveOrWrapperCodeTemplate extends JavaCodeTemplate<ParameterPrimitiveOrWrapperModel> {

    protected ParameterPrimitiveOrWrapperCodeTemplate() {
        super("java-templates/ParameterPrimitiveOrWrapper");
    }

    @Override
    Set<ImportModel> getImports() {
        return Set.of(new ImportModel(ClientMessage.class), new ImportModel(ValidationExecutor.class));
    }
}
