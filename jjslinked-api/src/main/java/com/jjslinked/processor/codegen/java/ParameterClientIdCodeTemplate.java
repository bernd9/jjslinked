package com.jjslinked.processor.codegen.java;

import com.jjslinked.model.ClientMessage;

import java.util.Set;

public class ParameterClientIdCodeTemplate extends JavaCodeTemplate {

    protected ParameterClientIdCodeTemplate() {
        super("java-templates/ParameterClientId");
    }

    @Override
    Set<String> getImports() {
        return Set.of(ClientMessage.class.getName());
    }
}
