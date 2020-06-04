package com.jjslinked.processor.codegen.java;

import java.util.Set;

public class MethodInvocationCodeTemplate extends JavaCodeTemplate<MethodInvocationModel> {

    protected MethodInvocationCodeTemplate() {
        super("java-templates/MethodInvocation");
    }

    @Override
    Set<ImportModel> getImports() {
        return null;
    }
}
