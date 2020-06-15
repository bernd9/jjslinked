package com.jjslinked.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.jjslinked.handlebar.HandlebarHelpers;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaSymbolTemplate<T> {

    private final Template template;
    private static Handlebars handlebars = new Handlebars();

    protected JavaSymbolTemplate(String template) {
        this.registerHelpers(handlebars);
        try {
            this.template = handlebars.compile(resourcePath(template));
        } catch (IOException e) {
            throw new RuntimeException(e);// TODO custom exception
        }
    }


    protected void registerHelpers(Handlebars handlebars) { // TODO Test
        handlebars.registerHelper("iterate", HandlebarHelpers::iterate);
        handlebars.registerHelper("firstToUpper", HandlebarHelpers::firstToUpper);
    }


    public void write(T model, JavaFileObject fileObject) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileObject.openOutputStream())) {
            writer.write(asString(model));
        }
    }

    public String asString(T model) throws IOException {
        return template.apply(model);
    }

    private String resourcePath(String resource) {
        return getClass().getPackageName().replace(".", "/") + "/" + resource;
    }

}
