package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class HandlerClassAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject handler;
    private JavaFileObject annotation;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new HandlerClassAnnotationProcessor());
        handler = JavaFileObjects.forResource("com/ejc/processor/TestMethodHandler.java");
        annotation = JavaFileObjects.forResource("com/ejc/processor/TestAnnotation.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(handler, annotation);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        ProcessorTestUtil.assertSuccess(compilation);
    }


}