package com.jjslinked.processor.codegen.java;

import java.util.Collections;
import java.util.Set;

interface JavaCodeModel {

    default Set<String> getImports() {
        return Collections.emptySet();
    }
}
