package com.ejc.processor;

import com.ejc.api.context.ClassReference;
import com.ejc.api.context.ModuleFactory;
import com.ejc.api.context.SimpleDependencyField;
import com.ejc.api.context.SingletonProvider;
import com.ejc.util.CollectionUtils;
import com.ejc.util.CollectorUtils;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.bindClassLoader;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class InheritedDependencyInjectionTest {

    private static final String DIRECTORY = "com/ejc/processor";


    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        ClassReference.flush();
        compiler = javac().withProcessors(new ModuleProcessor());
        files = ProcessorTestUtil.javaFileObjects(DIRECTORY, "InheritedDependencyInjectionTestApp.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        String moduleFactoryName = ModuleFactory.getQualifiedName("com.ejc.processor.InheritedDependencyInjectionTestApp");
        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<? extends ModuleFactory> factoryClass = (Class<? extends ModuleFactory>) classLoader.findClass(moduleFactoryName);
        ModuleFactory factory = factoryClass.getConstructor().newInstance();
        Module module = factory.getModule();

        ClassReference reference1 = ClassReference.getRef("com.ejc.processor.Singleton3");
        ClassReference reference2 = ClassReference.getRef("com.ejc.processor.Singleton4");

        SingletonProvider provider1 = module.getSingletonConstructors().stream()
                .filter(constructor -> constructor.getType().equals(reference1))
                .collect(CollectorUtils.toOnlyElement());

        SingletonProvider provider2 = module.getSingletonConstructors().stream()
                .filter(constructor -> constructor.getType().equals(reference2))
                .collect(CollectorUtils.toOnlyElement());

        assertThat(provider1.isSatisfied()).isTrue();
        assertThat(provider2.isSatisfied()).isTrue();

        SimpleDependencyField dependencyField = CollectionUtils.getOnlyElement(module.getDependencyFields());
        assertThat(dependencyField.getDeclaringType()).isEqualTo(reference1);
        //assertThat(dependencyField.getFieldType()).isEqualTo(reference2);

        ApplicationContextInitializer initializer = new ApplicationContextInitializer();
        initializer.addModule(module);

        initializer.initialize();
    }


}