package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationContextFactoryMultiDependencyTest {

    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        files = ProcessorTestUtil.javaFileObjects("com/ejc/processor/context/multi", "TestBean1.java", "TestBean2.java", "TestBean3.java", "TestBean3Impl.java", "TestInterface.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        Class<? extends ApplicationContextFactory> factoryClass = ProcessorTestUtil.getCompiledClass(compilation, ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
        ApplicationContext context = factory.createContext();

        Object testBean1 = context.getBean("com.ejc.processor.context.multi.TestBean1");
        assertThat(testBean1).isNotNull();

        Object fieldValue = ProcessorTestUtil.getFieldValue(testBean1, "test1");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(Collection.class);

        fieldValue = ProcessorTestUtil.getFieldValue(testBean1, "test2");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(Set.class);

        fieldValue = ProcessorTestUtil.getFieldValue(testBean1, "test3");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(List.class);

        fieldValue = ProcessorTestUtil.getFieldValue(testBean1, "test4");
        assertThat(fieldValue).isNotNull();
        assertThat(fieldValue).isInstanceOf(LinkedList.class);

    }

}