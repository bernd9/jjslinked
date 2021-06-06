package one.xis.processor;

import one.xis.context.ApplicationContext;
import one.xis.context.ApplicationContextFactory;
import one.xis.context.ModuleFactory;
import com.ejc.util.ClassUtils;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.google.testing.compile.Compiler.javac;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorTestUtil {

    public static Object getSingletonBySimpleClassName(String name, ApplicationContext context) {
        return context.getBeans().stream().filter(o -> o.getClass().getSimpleName().equals(name)).collect(CollectorUtils.toOnlyElement());
    }

    public static Optional<Object> getOptionalSingletonBySimpleClassName(String name, ApplicationContext context) {
        return context.getBeans().stream().filter(o -> o.getClass().getSimpleName().equals(name)).collect(CollectorUtils.toOnlyOptional());
    }


    public static ApplicationContext compileContext(String applicationClassName) throws Exception {
        Compiler compiler = javac().withProcessors(new ModuleFactoryProcessor());
        String applicationClassFilePath = applicationClassName.replace(".", "/");
        JavaFileObject fileObject = JavaFileObjects.forResource(applicationClassFilePath + ".java");
        Compilation compilation = compiler.compile(fileObject);
        ProcessorTestUtil.assertSuccess(compilation);
        String moduleFactoryName = ModuleFactory.getQualifiedName(applicationClassName);
        //ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
        FileObjectClassLoader classLoader = bindClassLoader(Thread.currentThread(), compilation);
        Class<ModuleFactory> moduleFactoryClass = (Class<ModuleFactory>) classLoader.findClass(moduleFactoryName);
        Class<?> applicationClass = classLoader.findClass(applicationClassName);
        ModuleFactory factory = ClassUtils.createInstance(moduleFactoryClass);
        ApplicationContextFactory applicationContextFactory = new ApplicationContextFactory(applicationClass, factory.getModule());
        ApplicationContext applicationContext = applicationContextFactory.createApplicationContext();
        //Thread.currentThread().setContextClassLoader(threadContextClassLoader);
        return applicationContext;
    }


    public static JavaFileObject[] javaFileObjects(String directory, String... javaFileNames) {
        List<JavaFileObject> fileObjects = javaFileObjectList(directory, javaFileNames);
        return fileObjects.toArray(new JavaFileObject[fileObjects.size()]);
    }

    public static FileObjectClassLoader bindClassLoader(Thread thread, Compilation compilation) {
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        FileObjectClassLoader fileObjectClassLoader;
        if (FileObjectClassLoader.class.isInstance(threadClassLoader)) {
            fileObjectClassLoader = (FileObjectClassLoader) threadClassLoader;
        } else {
            fileObjectClassLoader = new FileObjectClassLoader(Thread.currentThread().getContextClassLoader());
        }
        fileObjectClassLoader.setJavaFileObjects(compilation.generatedFiles());
        thread.setContextClassLoader(fileObjectClassLoader);
        return fileObjectClassLoader;
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
                .append(".").toString();
        //.append(ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME).toString();

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
