package com.jjslinked.processor.codegen.java;

import java.util.Collections;
import java.util.Set;

public class ClientImplCodeTemplate extends JavaCodeTemplate<ClientImplRenderModel> {

    protected ClientImplCodeTemplate() {
        super("ClientImpl");
    }

    @Override
    Set<String> getImports() {
        return Collections.emptySet();
    }
}
