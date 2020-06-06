package com.ejaf.processor.template;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JavaTemplate<T extends JavaTemplateModel> {

    void write(T model, Filer filer) {
        try {
            write(model, filer.createSourceFile(model.getJavaClassQualifiedName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void write(T model, JavaFileObject fileObject) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileObject.openOutputStream())) {
            
        }
    }

}
