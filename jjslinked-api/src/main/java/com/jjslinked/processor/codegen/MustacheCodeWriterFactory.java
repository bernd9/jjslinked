package com.jjslinked.processor.codegen;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import javax.annotation.processing.Filer;
import java.io.IOException;

public class MustacheCodeWriterFactory {

    private final Mustache mustache;
    private final Filer filer;

    public MustacheCodeWriterFactory(String template, Filer filer) {
        this.filer = filer;
        this.mustache = new DefaultMustacheFactory().compile(template);
    }

    public MustacheCodeWriter javaGenerator(String className) throws IOException {
        return new MustacheCodeWriter(filer.createSourceFile(className), mustache);
    }
}
