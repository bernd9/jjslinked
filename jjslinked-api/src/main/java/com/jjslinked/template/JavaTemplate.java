package com.jjslinked.template;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JavaTemplate<T extends JavaTemplateModel> extends JavaSymbolTemplate<T> {


    public JavaTemplate(String template) {
        super(template);
    }


    public void write(T model, Filer filer) {
        try {
            write(model, filer.createSourceFile(model.getJavaClass().getQualifiedName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(T model, JavaFileObject fileObject) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileObject.openOutputStream())) {
            writer.write(asString(model));
        }
    }

}
