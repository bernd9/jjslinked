package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

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
    void test() throws Exception {
        Compilation compilation = compiler.compile(testBean1, testBean2, testBean3, testBean3Impl);
        ProcessorTestUtil.assertSuccess(compilation);

        FileObjectClassLoader classLoader = new FileObjectClassLoader(Thread.currentThread().getContextClassLoader(), compilation.generatedFiles());
        //Thread.currentThread().setContextClassLoader(classLoader);
        Class<? extends ApplicationContextFactory> factoryClass = (Class<? extends ApplicationContextFactory>) classLoader.findClass(ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
        ApplicationContext context = factory.createContext();

        Object testBean1 = context.getBean("com.ejc.processor.context.singledependency.TestBean1");
        assertThat(testBean1).isNotNull();

        Object testBean2 = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "testBean2");
        assertThat(testBean2).isNotNull();
        assertThat(testBean2.getClass().getSimpleName()).isEqualTo("TestBean2");

        Object testBean3 = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "testBean3");
        assertThat(testBean3).isNotNull();
        assertThat(testBean3.getClass().getSimpleName()).isEqualTo("TestBean3Impl");

    }

}