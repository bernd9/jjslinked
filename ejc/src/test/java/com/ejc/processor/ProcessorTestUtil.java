package com.ejc.processor;

import com.ejc.ApplicationContextFactory;
import com.ejc.api.context.ApplicationContextInitializer;
import com.ejc.api.context.Module;
import com.ejc.api.context.ModuleFactory;
import com.ejc.util.CollectorUtils;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.google.testing.compile.Compiler.javac;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorTestUtil {

    public static Object getSingletonBySimpleClassName(String simpleClassName, ApplicationContextInitializer initializer) {
        return initializer.getSingletons().stream().filter(o -> o.getClass().getSimpleName().equals(simpleClassName)).collect(CollectorUtils.toOnlyElement());
    }

    public static ApplicationContextInitializer compileContext(String appName) throws Exception {
        Compiler compiler = javac().withProcessors(new ModuleProcessor());
        JavaFileObject fileObject = JavaFileObjects.forResource(appName.replace(".", "/") + ".java");
        Compilation compilation = compiler.compile(fileObject);
        ProcessorTestUtil.assertSuccess(compilation);

        String moduleFactoryName = ModuleFactory.getQualifiedName(appName);
        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<? extends ModuleFactory> factoryClass = (Class<? extends ModuleFactory>) classLoader.findClass(moduleFactoryName);
        ModuleFactory factory = factoryClass.getConstructor().newInstance();
        Module module = factory.getModule();

        ApplicationContextInitializer initializer = new ApplicationContextInitializer();
        initializer.addModule(module);

        initializer.initialize();
        return initializer;
    }


    public static JavaFileObject[] javaFileObjects(String directory, String... javaFileNames) {
        List<JavaFileObject> fileObjects = javaFileObjectList(directory, javaFileNames);
        return fileObjects.toArray(new JavaFileObject[fileObjects.size()]);
    }

    public static FileObjectClassLoader bindClassLoader(Thread thread, Compilation compilation) {
        FileObjectClassLoader classLoader = new FileObjectClassLoader(Thread.currentThread().getContextClassLoader(), compilation.generatedFiles());
        thread.setContextClassLoader(classLoader);
        return classLoader;
    }

    public static <T> Class<T> getCompiledClass(Compilation compilation, String name) {
        FileObjectClassLoader classLoader = new FileObjectClassLoader(Thread.currentThread().getContextClassLoader(), compilation.generatedFiles());
        try {
            return (Class<T>) classLoader.findClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<JavaFileObject> javaFileObjectList(String directory, String... javaFileNames) {
        return Arrays.stream(javaFileNames)
                .map(name -> directory + "/" + name)
                .map(JavaFileObjects::forResource)
                .collect(Collectors.toList());
    }


    public static void assertSuccess(Compilation compilation) {
        if (compilation.status() != Compilation.Status.SUCCESS) {
            throw new RuntimeException(compilation.errors().stream().map(ProcessorTestUtil::asString).collect(Collectors.joining("\n\n")));
        }
    }

    public static String asString(Diagnostic<?> diagnostic) {
        StringBuilder s = new StringBuilder();
        if (diagnostic.getSource() != null)
            s.append(diagnostic.getSource());

        s.append(" in line ").append(diagnostic.getLineNumber())
                .append(": ")
                .append(diagnostic.getMessage(Locale.US)).toString();
        return s.toString();
    }

    public static Collection<String> getSources(Compilation compilation) {
        return compilation.generatedSourceFiles().stream().map(ProcessorTestUtil::getSource).collect(Collectors.toList());
    }

    public static String getSource(JavaFileObject fileObject) {
        StringWriter writer = new StringWriter();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileObject.openInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String src = writer.toString();
        return src;
    }

    public static String getContextFactoryDefaultName() {
        return new StringBuilder()
                //.append(ApplicationContextFactoryProcessor.PACKAGE)
                .append(".")
                .append(ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME).toString();

    }


    public static Object getFieldValue(Object bean, String name) {
        for (Class<?> c = bean.getClass(); c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(bean);
            } catch (Exception e) {

            }
        }
        return null;
    }
}
