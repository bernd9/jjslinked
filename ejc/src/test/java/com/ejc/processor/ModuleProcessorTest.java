package com.ejc.processor;

import com.ejc.api.context.Module;
import com.ejc.api.context.*;
import com.ejc.util.CollectionUtils;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.ejc.processor.ProcessorTestUtil.bindClassLoader;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class ModuleProcessorTest {

    private static final String DIRECTORY = "com/ejc/processor";

    @Nested
    class FieldInjectionTest {
        private Compiler compiler;
        private JavaFileObject[] files;

        @BeforeEach
        void init() {
            compiler = javac().withProcessors(new ModuleProcessor());
            files = ProcessorTestUtil.javaFileObjects(DIRECTORY, "FieldInjectionTestApp.java");
        }

        @Test
        void test() throws Exception {
            Compilation compilation = compiler.compile(files);
            ProcessorTestUtil.assertSuccess(compilation);

            String moduleFactoryName = ModuleFactory.getQualifiedName("com.ejc.processor.FieldInjectionTestApp");
            FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
            Class<? extends ModuleFactory> factoryClass = (Class<? extends ModuleFactory>) classLoader.findClass(moduleFactoryName);
            ModuleFactory factory = factoryClass.getConstructor().newInstance();
            Module module = factory.getModule();

            assertThat(module.getSingletonConstructors().size()).isEqualTo(2);
            assertThat(module.getDependencyFields().size()).isEqualTo(1);

            ClassReference reference1 = ClassReference.getRef("com.ejc.processor.Singleton1");
            ClassReference reference2 = ClassReference.getRef("com.ejc.processor.Singleton2");

            SingletonProvider provider1 = module.getSingletonConstructors().get(reference1);
            SingletonProvider provider2 = module.getSingletonConstructors().get(reference2);

            assertThat(provider1.isSatisfied()).isTrue();
            assertThat(provider2.isSatisfied()).isTrue();

            SimpleDependencyField dependencyField = CollectionUtils.getOnlyElement(module.getDependencyFields().get(reference1));
            assertThat(dependencyField.getDeclaringType()).isEqualTo(reference1);
            assertThat(dependencyField.getFieldType()).isEqualTo(reference2);

            ApplicationContextInitializer initializer = new ApplicationContextInitializer();
            initializer.addModule(module);

            initializer.initialize();
        }

    }


}