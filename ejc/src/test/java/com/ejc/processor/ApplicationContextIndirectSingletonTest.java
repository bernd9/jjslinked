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
class ApplicationContextIndirectSingletonTest {

    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        //compiler = javac().withProcessors(new ApplicationContextFactoryProcessor());
        files = ProcessorTestUtil.javaFileObjects("com/ejc/processor/context/single/indirect", "TestBean.java", "TestAnnotation.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<? extends ApplicationContextFactory> factoryClass = (Class<? extends ApplicationContextFactory>) classLoader.findClass(ProcessorTestUtil.getContextFactoryDefaultName());
        ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
        ApplicationContext context = factory.createContext();
        Object testBean1 = context.getBean("com.ejc.processor.context.single.indirect.TestBean");
        assertThat(testBean1).isNotNull();
    }

}