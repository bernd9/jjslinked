package com.jjslinked.processor.registry;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.jjslinked.processor.ProcessorTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;

class RegistryAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean;
    private JavaFileObject registry;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new RegistryAnnotationProcessor());
        bean = JavaFileObjects.forResource("com/jjslinked/processor/registry/TestBean.java");
        //registry = JavaFileObjects.forResource("com/jjslinked/processor/registry/TestRegistry1.java");

    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean);
        //ProcessorTestUtilOld.getSources(compilation).forEach(System.out::println);
        ProcessorTestUtil.assertSuccess(compilation);
    }
}