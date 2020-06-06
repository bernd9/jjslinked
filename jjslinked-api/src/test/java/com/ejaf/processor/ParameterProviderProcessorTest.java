package com.ejaf.processor;

import com.ejaf.processor.parameter.ParameterProviderProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.assertTrue;

class ParameterProviderProcessorTest {

    private Compiler compiler;
    private JavaFileObject annotation;
    private JavaFileObject provider;
    private JavaFileObject bean;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ParameterProviderProcessor());
        annotation = JavaFileObjects.forResource("com/ejaf/processor/ParameterProviderTestAnnotation.java");
        provider = JavaFileObjects.forResource("com/ejaf/processor/ParameterProviderTestProvider.java");
        bean = JavaFileObjects.forResource("com/ejaf/processor/ParameterProviderTestBean.java");
    }

    @Test
    void test() throws IOException {
        Compilation compilation = compiler.compile(annotation, provider, bean);
        assertTrue(compilation.status() == Compilation.Status.SUCCESS);
        JavaFileObject javaFileObject = compilation.generatedSourceFile("com.ejaf.generated.ParameterProvider0").orElseThrow();
        // TODO

    }


}