package com.jjslinked.template;

import javax.annotation.processing.Filer;
import java.io.IOException;

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

}
