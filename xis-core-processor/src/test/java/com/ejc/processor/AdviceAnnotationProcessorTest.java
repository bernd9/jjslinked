package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;


class AdviceAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceAnnotationProcessor());
        files = ProcessorTestUtil.javaFileObjects("com/ejc/processor/advice", "Test123Advice.java", "AdviceTestBean.java");
    }

    @Test
    void test() {
        // TODO
        Compilation compilation = compiler.compile(files);
        assertSuccess(compilation);
        //assertEquals(1, compilation.generatedSourceFiles().size());
    }

}