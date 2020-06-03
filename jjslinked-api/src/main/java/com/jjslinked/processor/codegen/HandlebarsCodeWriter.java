package com.jjslinked.processor.codegen;

import com.github.jknack.handlebars.Template;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

public class HandlebarsCodeWriter implements Closeable {

    private final Template template;
    private final PrintWriter writer;

    HandlebarsCodeWriter(PrintWriter printWriter, Template template) {
        this.template = template;
        this.writer = printWriter;
    }

    public void write(Object context) throws IOException {
        this.writer.write(template.apply(context));
    }

    @Override
    public void close() {
        writer.close();
    }
}
