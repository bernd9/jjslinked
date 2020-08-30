package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextFactoryCollectionDependencyTest {

    private Compiler compiler;
    private JavaFileObject testBean1;
    private JavaFileObject testBean2;
    private JavaFileObject testBean3;
    private JavaFileObject testBean3Impl;
    private JavaFileObject testInterface;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        testBean1 = JavaFileObjects.forResource("com/ejc/processor/context/colldependency/TestBean1.java");
        testBean2 = JavaFileObjects.forResource("com/ejc/processor/context/colldependency/TestBean2.java");
        testBean3 = JavaFileObjects.forResource("com/ejc/processor/context/colldependency/TestBean3.java");
        testBean3Impl = JavaFileObjects.forResource("com/ejc/processor/context/colldependency/TestBean3Impl.java");
        testInterface = JavaFileObjects.forResource("com/ejc/processor/context/colldependency/TestInterface.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(testInterface, testBean1, testBean2, testBean3, testBean3Impl);
        ProcessorTestUtil.assertSuccess(compilation);

        FileObjectClassLoader classLoader = new FileObjectClassLoader(Thread.currentThread().getContextClassLoader(), compilation.generatedFiles());
        Class<? extends ApplicationContextFactory> factoryClass = (Class<? extends ApplicationContextFactory>) classLoader.findClass(ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
        ApplicationContext context = factory.createContext();

        Object testBean1 = context.getBean("com.ejc.processor.context.colldependency.TestBean1");
        assertThat(testBean1).isNotNull();

        Object fieldValue = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "test1");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(Collection.class);

        fieldValue = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "test2");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(Set.class);

        fieldValue = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "test3");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(List.class);

        fieldValue = ProcessorTestUtil.getDeclaredFieldValue(testBean1, "test4");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(LinkedList.class);

    }

}