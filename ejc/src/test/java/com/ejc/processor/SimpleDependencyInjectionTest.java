package com.ejc.processor;

import com.ejc.api.context.Module;
import com.ejc.api.context.*;
import com.ejc.util.CollectionUtils;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
import com.google.common.base.Functions;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ejc.processor.ProcessorTestUtil.bindClassLoader;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleDependencyInjectionTest {

    private static final String DIRECTORY = "com/ejc/processor/simpledependency";


    private Compiler compiler;
    private JavaFileObject[] files;

    @BeforeEach
    void init() {
        ClassReference.flush();
        compiler = javac().withProcessors(new ModuleProcessor());
        files = ProcessorTestUtil.javaFileObjects(DIRECTORY, "SimpleDependencyInjectionTestApp.java");
    }

    @Test
    void test() throws Exception {
        Compilation compilation = compiler.compile(files);
        ProcessorTestUtil.assertSuccess(compilation);

        String moduleFactoryName = ModuleFactory.getQualifiedName("com.ejc.processor.simpledependency.SimpleDependencyInjectionTestApp");
        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<? extends ModuleFactory> factoryClass = (Class<? extends ModuleFactory>) classLoader.findClass(moduleFactoryName);
        ModuleFactory factory = factoryClass.getConstructor().newInstance();
        Module module = factory.getModule();

        //assertThat(module.getSingletonConstructors().size()).isEqualTo(2);
        assertThat(module.getDependencyFields().size()).isEqualTo(1);

        ClassReference reference1 = ClassReference.getRef("com.ejc.processor.simpledependency.SimpleDependencyInjectionTestApp$SimpleDependencySingleton1");
        ClassReference reference2 = ClassReference.getRef("com.ejc.processor.simpledependency.SimpleDependencyInjectionTestApp$SimpleDependencySingleton2");

        SingletonProvider provider1 = module.getSingletonConstructors().stream()
                .filter(constructor -> constructor.getType().equals(reference1))
                .collect(CollectorUtils.toOnlyElement());

        SingletonProvider provider2 = module.getSingletonConstructors().stream()
                .filter(constructor -> constructor.getType().equals(reference2))
                .collect(CollectorUtils.toOnlyElement());
        

        SimpleDependencyField dependencyField = CollectionUtils.getOnlyElement(module.getDependencyFields());
        assertThat(dependencyField.getDeclaringType()).isEqualTo(reference1);
        assertThat(dependencyField.getFieldType()).isEqualTo(reference2);

        ApplicationContextInitializer initializer = new ApplicationContextInitializer();
        initializer.addModule(module);

        initializer.initialize();

        Map<String, Object> beansByName = initializer.getSingletons().stream().collect(Collectors.toMap(o -> o.getClass().getName(), Functions.identity()));
        Object singleton1 = beansByName.get("com.ejc.processor.simpledependency.SimpleDependencyInjectionTestApp$SimpleDependencySingleton1");

        assertThat(FieldUtils.getFieldValue(singleton1, "singleton2").getClass().getName()).isEqualTo("com.ejc.processor.simpledependency.SimpleDependencyInjectionTestApp$SimpleDependencySingleton2");
    }


}