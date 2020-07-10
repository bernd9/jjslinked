package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class SystemPropertyAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean1;
    private JavaFileObject bean2;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new SystemPropertyAnnotationProcessor());
        bean1 = JavaFileObjects.forResource("com/ejc/processor/Test1.java");
        bean2 = JavaFileObjects.forResource("com/ejc/processor/Test2.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean1, bean2);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        ProcessorTestUtil.assertSuccess(compilation);
    }

}