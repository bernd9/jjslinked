package com.ejaf.processor;

import com.ejaf.processor.parameter.ParameterProviderProcessor;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class ParameterProviderProcessorTest {


    private Compiler compiler;
    private JavaFileObject bean;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ParameterProviderProcessor());
        bean = JavaFileObjects.forResource("com.ejaf.processor.ParameterProviderTestBean");
    }

    @Test
    void test() {
        //Compilation compilation = compiler.compile(javaFileObject);
        //System.out.println(compilation.status());
    }


}