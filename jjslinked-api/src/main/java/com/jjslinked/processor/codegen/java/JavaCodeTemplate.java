package com.jjslinked.processor.codegen.java;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import lombok.Setter;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

abstract class JavaCodeTemplate<M> {

    private final Template template;
    private static Handlebars handlebars = new Handlebars();

    @Setter
    private M context;

    static {
        handlebars.registerHelpers(new Helpers());
    }

    protected JavaCodeTemplate(String template) {
        this.registerHelpers(handlebars);
        try {
            this.template = handlebars.compile(template);
        } catch (IOException e) {
            throw new RuntimeException(e);// TODO custom exception
        }
    }


    protected void registerHelpers(Handlebars handlebars) {

    }

    abstract Set<ImportModel> getImports();

    void write(OutputStream out) throws IOException {
        write(new PrintWriter(out));
    }

    void write(Writer writer) throws IOException {
        writer.write(template.apply(context));
    }

    void write(FileObject fileObject) throws IOException {
        try (OutputStream out = fileObject.openOutputStream()) {
            write(out);
        }
    }

    public static class Helpers {

        /*
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

        public CharSequence ifEqual(Object value1, Object value2, Options options) throws IOException {
            if (options.params.length > 0) {
                value2 = options.param(0);
            }
            boolean evaluate;
            if (value1 == null) {
                evaluate = value2 == null;
            } else if (value2 == null) {
                evaluate = false;
            } else {
                evaluate = value1.toString().equals(value2.toString());
            }
            if (evaluate) {
                return options.fn();
            }
            return "";
        }

        public void setVar(@NonNull String name, @NonNull Object value, Options options) {
            options.data(name.toString(), value.toString());
        }

        public String getVar(@NonNull String name, Options options) {
            return options.data(name);
        }


         */
    }


}
