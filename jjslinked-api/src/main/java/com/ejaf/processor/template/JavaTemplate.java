package com.ejaf.processor.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JavaTemplate<T extends JavaTemplateModel> {


    private final Template template;
    private static Handlebars handlebars = new Handlebars();


    protected JavaTemplate(String template) {
        this.registerHelpers(handlebars);
        try {
            this.template = handlebars.compile(resourcePath(template));
        } catch (IOException e) {
            throw new RuntimeException(e);// TODO custom exception
        }
    }


    protected void registerHelpers(Handlebars handlebars) {

    }


    public void write(T model, Filer filer) {
        try {
            write(model, filer.createSourceFile(model.getJavaClassQualifiedName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(T model, JavaFileObject fileObject) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileObject.openOutputStream())) {
            writer.write(template.apply(model));
        }
    }

    private String resourcePath(String resource) {
        return getClass().getPackageName().replace(".", "/") + "/" + resource;
    }

}
