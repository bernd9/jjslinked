package com.jjslinked.processor.codegen.java;

import java.util.Collections;
import java.util.Set;

public class MethodInvocationCodeTemplate extends JavaCodeTemplate {

    protected MethodInvocationCodeTemplate() {
        super("MethodInvocation");
    }

    @Override
    Set<String> getImports() {
        return Collections.emptySet();
    }
}
