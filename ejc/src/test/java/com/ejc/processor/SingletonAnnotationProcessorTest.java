package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SingletonAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean1;
    private JavaFileObject bean2;
    private JavaFileObject bean3;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new SingletonAnnotationProcessor());
        bean1 = JavaFileObjects.forResource("com/ejc/processor/Test1.java");
        bean2 = JavaFileObjects.forResource("com/ejc/processor/Test2.java");
        bean3 = JavaFileObjects.forResource("com/ejc/processor/Test3.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean1, bean2, bean3);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        assertEquals(Compilation.Status.SUCCESS, compilation.status());
        assertEquals(3, compilation.generatedSourceFiles().size());
    }

}