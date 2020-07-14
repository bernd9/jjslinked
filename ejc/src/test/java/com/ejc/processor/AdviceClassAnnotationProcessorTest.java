package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdviceClassAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject handler;
    private JavaFileObject annotation;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceClassAnnotationProcessor());
        handler = JavaFileObjects.forResource("com/ejc/processor/TestMethodHandler.java");
        annotation = JavaFileObjects.forResource("com/ejc/processor/TestAnnotation.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(handler, annotation);
        assertSuccess(compilation);
        assertEquals(1, compilation.generatedSourceFiles().size());
    }


}