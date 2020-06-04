package com.jjslinked.processor.codegen.java;

import com.jjslinked.model.ClientMessage;
import com.jjslinked.validation.ValidationExecutor;

import java.util.Set;

public class ParameterPrimitiveOrWrapperCodeTemplate extends JavaCodeTemplate {

    protected ParameterPrimitiveOrWrapperCodeTemplate() {
        super("java-templates/ParameterPrimitiveOrWrapper");
    }

    @Override
    Set<String> getImports() {
        return Set.of(ClientMessage.class.getName(), ValidationExecutor.class.getName());
    }
}
