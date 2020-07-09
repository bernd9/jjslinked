package com.ejc.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class ApplicationContextFactoryProcessorTest {

    private Compiler compiler;
    private JavaFileObject loader;
    private JavaFileObject injector;
    private JavaFileObject initializer;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        loader = JavaFileObjects.forResource("com/ejc/generated/singleton/TestSingletonLoader.java");
        injector = JavaFileObjects.forResource("com/ejc/generated/inject/TestInjector.java");
        initializer = JavaFileObjects.forResource("com/ejc/generated/init/TestInitializer.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(loader, injector, initializer);
        ProcessorTestUtil.assertSuccess(compilation);
    }

}