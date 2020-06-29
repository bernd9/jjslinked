package com.jjslinked.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.jjslinked.handlebar.HandlebarHelpers;

import java.io.IOException;

public class JavaSymbolTemplate<T> {

    private final Template template;
    private static Handlebars handlebars = new Handlebars();

    static {
        handlebars.registerHelper("iterate", HandlebarHelpers::iterate);
        handlebars.registerHelper("firstToUpper", HandlebarHelpers::firstToUpper);
    }

    public JavaSymbolTemplate(String template) {
        try {
            this.template = handlebars.compile(resourcePath(template));
        } catch (IOException e) {
            throw new RuntimeException(e);// TODO custom exception
        }
    }

    public String asString(T model) throws IOException {
        return template.apply(model);
    }

    private String resourcePath(String resource) {
        return getClass().getPackageName().replace(".", "/") + "/" + resource;
    }

}
