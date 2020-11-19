package com.ejc.processor;

import com.ejc.ApplicationClassHolder;
import com.ejc.javapoet.JavaWriter;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collections;
import java.util.Optional;

class ApplicationClassHolderWriter extends JavaWriter {

    private final String appClassName;

    public ApplicationClassHolderWriter(String appClassName, ProcessingEnvironment processingEnvironment) {
        super("ApplicationClassHolderImpl", Optional.of("com.ejc"), Optional.of(ApplicationClassHolder.class), processingEnvironment, Collections.emptySet());
        this.appClassName = appClassName;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("super(\"$L\")", appClassName);
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {

    }
}
