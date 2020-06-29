package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class ApplicationContextProcessorInjectAllTest {

    private Compiler compiler;
    private JavaFileObject[] javaFileObjects;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextProcessor());
        javaFileObjects = ProcessorTestUtil.javaFileObjects("com/ejc/processor",
                "Implementation1.java",
                "Implementation2.java", "Implementation3.java", "Interf.java", "TestInjectAll.java");

    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(javaFileObjects);
        ProcessorTestUtil.assertSuccess(compilation);

    }

}