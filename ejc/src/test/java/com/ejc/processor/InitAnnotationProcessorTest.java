package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class InitAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new InitAnnotationProcessor());
        bean = JavaFileObjects.forResource("com/ejc/processor/Test3.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        ProcessorTestUtil.assertSuccess(compilation);
    }


}