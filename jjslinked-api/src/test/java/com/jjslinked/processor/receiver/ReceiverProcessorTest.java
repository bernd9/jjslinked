package com.jjslinked.processor.receiver;

import com.ejaf.processor.ProcessorTestUtil;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejaf.processor.ProcessorTestUtil.assertSuccess;
import static com.google.testing.compile.Compiler.javac;

class ReceiverProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ReceiverAnnotationProcessor());
        bean = JavaFileObjects.forResource("com/jjslinked/processor/receiver/TestBean.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean);
        ProcessorTestUtil.getSources(compilation).forEach(System.out::println);
        assertSuccess(compilation);
    }


}