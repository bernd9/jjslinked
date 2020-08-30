package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class ApplicationContextFactorySingleDependencyTest {

    private Compiler compiler;
    private JavaFileObject testBean1;
    private JavaFileObject testBean2;
    private JavaFileObject testBean3;
    private JavaFileObject testBean3Impl;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        testBean1 = JavaFileObjects.forResource("com/ejc/processor/context/singledependency/TestBean1.java");
        testBean2 = JavaFileObjects.forResource("com/ejc/processor/context/singledependency/TestBean2.java");
        testBean3 = JavaFileObjects.forResource("com/ejc/processor/context/singledependency/TestBean3.java");
        testBean3Impl = JavaFileObjects.forResource("com/ejc/processor/context/singledependency/TestBean3Impl.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(testBean1, testBean2, testBean3Impl);
        ProcessorTestUtil.assertSuccess(compilation);
        // TODO check results. Tool to load compiled class ?
    }

}