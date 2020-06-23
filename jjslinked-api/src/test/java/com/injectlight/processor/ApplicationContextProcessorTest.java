package com.injectlight.processor;

import com.ejaf.processor.ProcessorTestUtil;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejaf.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;

class ApplicationContextProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean1;
    private JavaFileObject bean2;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextProcessor());
        bean1 = JavaFileObjects.forResource("com/injectlight/processor/Test1.java");
        bean2 = JavaFileObjects.forResource("com/injectlight/processor/Test2.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean1, bean2);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        assertSuccess(compilation);
    }

}