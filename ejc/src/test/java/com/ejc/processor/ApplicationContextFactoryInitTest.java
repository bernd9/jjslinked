package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextFactoryInitTest {

    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        files = ProcessorTestUtil.javaFileObjects("com/ejc/processor/context/init", "TestBean1.java", "TestBean2.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        Class<? extends ApplicationContextFactory> factoryClass = ProcessorTestUtil.getCompiledClass(compilation, ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
        ApplicationContext context = factory.createContext();

        Object testBean1 = context.getBean("com.ejc.processor.context.init.TestBean1");
        assertThat(testBean1).isNotNull();

        Integer test = (Integer) ProcessorTestUtil.getFieldValue(testBean1, "test");
        assertThat(test).isEqualTo(101);
    }

}