package com.jjslinked.processor.codegen.java;

import java.util.Collections;
import java.util.Set;

interface ParameterCodeModel extends JavaCodeModel {
    default Set<String> getImports() {
        return Collections.emptySet();
    }
}
