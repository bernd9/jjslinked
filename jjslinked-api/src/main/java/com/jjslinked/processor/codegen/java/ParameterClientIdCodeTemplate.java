package com.jjslinked.processor.codegen.java;

import com.jjslinked.model.ClientMessage;

import java.util.Set;

public class ParameterClientIdCodeTemplate extends JavaCodeTemplate<ParameterClientIdModel> {

    protected ParameterClientIdCodeTemplate() {
        super("java-templates/ParameterClientId");
    }

    @Override
    Set<ImportModel> getImports() {
        return Set.of(new ImportModel(ClientMessage.class));
    }
}
