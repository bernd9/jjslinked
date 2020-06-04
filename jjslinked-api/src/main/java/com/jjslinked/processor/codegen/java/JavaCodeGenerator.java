package com.jjslinked.processor.codegen.java;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Set;

public class JavaCodeGenerator<T extends JavaCodeTemplate<C>, C> {

    private T template;

    JavaCodeGenerator(T template) {
        this.template = template;
    }

    void write(C context, FileObject fileObject) throws IOException {
        template.setContext(context);
        try (OutputStream out = fileObject.openOutputStream()) {
            template.write(out);
        }
    }

    String asString(C context) throws IOException {
        template.setContext(context);
        try (StringWriter sw = new StringWriter()) {
            template.write(sw);
            return sw.toString();
        }
    }

    Set<ImportModel> getImports() {
        return template.getImports();
    }
}
