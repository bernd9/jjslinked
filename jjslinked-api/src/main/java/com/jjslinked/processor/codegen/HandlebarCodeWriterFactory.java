package com.jjslinked.processor.codegen;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HandlebarCodeWriterFactory {

    private final Template template;
    private static Handlebars handlebars = new Handlebars();

    static {
        handlebars.registerHelpers(new Helpers());
        handlebars.registerHelper("iterate1", new Helper<Object>() {
            @Override
            public Object apply(Object context, Options options) throws IOException {
                return null;
            }
        });
    }

    public HandlebarCodeWriterFactory(String template) {
        try {
            this.template = handlebars.compile(template);
        } catch (IOException e) {
            throw new RuntimeException(e);// TODO custosm exception
        }
    }

    public HandlebarsCodeWriter javaGenerator(String className, Filer filer) throws IOException {
        return javaGenerator(filer.createSourceFile(className));
    }

    public HandlebarsCodeWriter javaGenerator(JavaFileObject fileObject) throws IOException {
        return javaGenerator(new PrintWriter(fileObject.openOutputStream()));
    }

    public HandlebarsCodeWriter javaGenerator(PrintWriter printWriter) {
        return new HandlebarsCodeWriter(printWriter, template);
    }

    public static class Helpers {

        public CharSequence iterate(Object context, Options options) throws IOException {
            if (context instanceof Iterable) {
                String delimiter = options.params.length > 0 ? options.param(0) : "";
                return StreamSupport.stream(((Iterable<Object>) context).spliterator(), false).map(e -> {
                    try {
                        return options.fn(e);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }).collect(Collectors.joining(delimiter));
            } else {
                return options.fn();
            }

        }


    }

    @Getter
    @RequiredArgsConstructor
    public static class IteratedElement {
        private final Object value;
        private boolean more;

        public boolean hasMore() {
            return more;
        }
    }

    /*
    Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("mytemplate");

System.out.println(template.apply("Handlebars.java"));
     */
}
