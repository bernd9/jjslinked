package com.jjslinked.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processor.ClientProcessor;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class ClientProcessorTest {

    private Compiler compiler;
    private JavaFileObject javaFileObject;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ClientProcessor());
        javaFileObject = JavaFileObjects.forResource("Test.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(javaFileObject);
        System.out.println(compilation.status());
    }


}