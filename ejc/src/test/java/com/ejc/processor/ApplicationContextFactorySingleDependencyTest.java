package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.ApplicationContextFactory;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.bindClassLoader;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class ApplicationContextFactorySingleDependencyTest {

    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        //compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        files = ProcessorTestUtil.javaFileObjects("com/ejc/processor/context/single", "TestApplication.java", "TestBean1a.java", "TestBean2.java", "TestBean3.java", "TestBean3Impl.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<? extends ApplicationContextFactory> factoryClass = (Class<? extends ApplicationContextFactory>) classLoader.findClass(ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();

        ApplicationContext context = factory.createContext();

        Object testBean1 = context.getBean("com.ejc.processor.context.single.TestBean1");
        assertThat(testBean1).isNotNull();

        Object testBean2 = ProcessorTestUtil.getFieldValue(testBean1, "testBean2");
        assertThat(testBean2).isNotNull();
        assertThat(testBean2.getClass().getSimpleName()).isEqualTo("TestBean2");

        Object testBean3 = ProcessorTestUtil.getFieldValue(testBean1, "testBean3");
        assertThat(testBean3).isNotNull();
        assertThat(testBean3.getClass().getSimpleName()).isEqualTo("TestBean3Impl");

        Object testBean1a = context.getBean("com.ejc.processor.context.single.TestBean1a");
        assertThat(testBean1).isNotNull();

        testBean2 = ProcessorTestUtil.getFieldValue(testBean1a, "testBean2");
        assertThat(testBean2).isNotNull();
        assertThat(testBean2.getClass().getSimpleName()).isEqualTo("TestBean2");

        testBean3 = ProcessorTestUtil.getFieldValue(testBean1a, "testBean3");
        assertThat(testBean3).isNotNull();
        assertThat(testBean3.getClass().getSimpleName()).isEqualTo("TestBean3Impl");

    }

}