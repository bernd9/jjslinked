package com.ejc.processor;

import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;


class AdviceAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject advice;
    private JavaFileObject annotation;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceAnnotationProcessor());
        // advice = JavaFileObjects.forResource("com/ejc/processor/TestMethodHandler.java");
        // annotation = JavaFileObjects.forResource("com/ejc/processor/TestAnnotation.java");
    }

    @Test
    void test() {
        // TODO
        // Compilation compilation = compiler.compile(advice, annotation);
        //assertSuccess(compilation);
        //assertEquals(1, compilation.generatedSourceFiles().size());
    }

}