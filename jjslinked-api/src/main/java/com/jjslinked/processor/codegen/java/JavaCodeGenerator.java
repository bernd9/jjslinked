package com.jjslinked.processor.codegen.java;

import lombok.Getter;

import java.io.IOException;
import java.io.Writer;


public abstract class JavaCodeGenerator<T extends JavaCodeTemplate, M, R> {

    @Getter
    private T template;

    JavaCodeGenerator(T template) {
        this.template = template;
    }

    void write(M context, Writer writer) throws IOException {
        template.write(toRenderModel(context), writer);
    }

    String asString(M context) {
        return template.asString(toRenderModel(context));
    }

    abstract R toRenderModel(M model);


}
