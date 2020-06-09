package com.ejaf.processor.parameter;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejaf.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;

class ParameterProviderProcessorTest {

    private Compiler compiler;
    private JavaFileObject annotation;
    private JavaFileObject provider;
    private JavaFileObject bean;
    private JavaFileObject event;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ParameterProviderProcessor());
        annotation = JavaFileObjects.forResource("com/ejaf/processor/parameter/TestAnnotation.java");
        provider = JavaFileObjects.forResource("com/ejaf/processor/parameter/TestProvider.java");
        bean = JavaFileObjects.forResource("com/ejaf/processor/parameter/TestBean.java");
        event = JavaFileObjects.forResource("com/ejaf/processor/parameter/TestEvent.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(annotation, provider, bean, event);
        assertSuccess(compilation);
    }


}