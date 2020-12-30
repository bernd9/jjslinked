package com.ejc.processor;

import com.ejc.api.context.ApplicationRunnerBase;
import com.ejc.api.context.ClassReference;
import com.ejc.javapoet.JavaWriter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collections;
import java.util.Optional;

class ApplicationRunnerWriter extends JavaWriter {

    private final String appClassName;

    public ApplicationRunnerWriter(String appClassName, ProcessingEnvironment processingEnvironment) {
        super("ApplicationRunnerImpl", Optional.of("com.ejc"), Optional.of(ClassName.get(ApplicationRunnerBase.class)), processingEnvironment, Collections.emptySet());
        this.appClassName = appClassName;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("super($T.getRef(\"$L\"))", ClassReference.class, appClassName);
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {

    }
}
