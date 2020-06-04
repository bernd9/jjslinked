package com.jjslinked.processor.codegen.java;

import com.github.jknack.handlebars.Handlebars;

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

    @Override
    protected void registerHelpers(Handlebars handlebars) {
        handlebars.registerHelper("firstToLower", HandlebarHelpers::firstToLower);
        handlebars.registerHelper("iterate", HandlebarHelpers::iterate);
        super.registerHelpers(handlebars);
    }
}
