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
    private JavaFileObject bean;


    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceClassAnnotationProcessor(TestAnnotation.class, TestMethodHandler.class));
        bean = JavaFileObjects.forResource("com/ejc/processor/AdviceClassTestBean.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean);
        assertSuccess(compilation);
        assertEquals(1, compilation.generatedSourceFiles().size());
    }


}