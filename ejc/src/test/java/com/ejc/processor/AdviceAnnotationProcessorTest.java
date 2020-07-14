package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;


class AdviceAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject advice;
    private JavaFileObject bean;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceAnnotationProcessor());
        advice = JavaFileObjects.forResource("com/ejc/processor/Test123Advice.java");
        bean = JavaFileObjects.forResource("com/ejc/processor/AdviceTestBean.java");
    }

    @Test
    void test() {
        // TODO
        Compilation compilation = compiler.compile(advice, bean);
        assertSuccess(compilation);
        //assertEquals(1, compilation.generatedSourceFiles().size());
    }

}