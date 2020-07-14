package com.ejc.processor;

import com.ejc.MethodAdvice;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class MethodAnnotationProcessor extends AbstractProcessor {
    private static final String PACKAGE = "com.ejc.generated.proxies";

    // TODO Some parts of the code will be usefull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ProcessorConfig.getMethodAnnotations().keySet().stream().map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processAnnotation(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processAnnotation(RoundEnvironment roundEnv) {
        Map<TypeElement, Set<ExecutableElement>> proxyMethods = new HashMap<>();
        roundEnv.getElementsAnnotatedWithAny(ProcessorConfig.getMethodAnnotations().keySet())
                .stream()
                .map(ExecutableElement.class::cast)
                .forEach(executable -> {
                    TypeElement owner = (TypeElement) executable.getEnclosingElement();
                    proxyMethods.computeIfAbsent(owner, type -> new HashSet<>()).add(executable);
                });
        writeProxyAndLoader(proxyMethods);
    }

    private void writeProxyAndLoader(Map<TypeElement, Set<ExecutableElement>> proxyMethods) {
        proxyMethods.entrySet().forEach(e -> {
            writeProxy(e.getKey(), e.getValue());
            writeSingletonLoader(e.getKey());
        });
    }

    private String proxySimpleName(TypeElement orig) {
        return orig.getSimpleName().toString() + "Impl";
    }

    private String proxyQualifiedName(TypeElement orig) {
        return orig.getQualifiedName().toString().replace(orig.getSimpleName().toString(), proxySimpleName(orig));
    }

    private String proxyPackage(TypeElement orig) {
        String qualifiedName = orig.getQualifiedName().toString();
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return null;
        }
        return qualifiedName.substring(0, i);
    }


    private void writeProxy(TypeElement e, Set<ExecutableElement> proxyMethods) {
        log("processing %s", proxyQualifiedName(e));
        String packageName = proxyPackage(e);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(proxyQualifiedName(e)).openOutputStream()))) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println("import com.ejc.*;");
                out.println("import com.ejc.processor.*;");
                out.printf("public class %s extends %s", proxySimpleName(e), e.getSimpleName());
                out.println("{");
                proxyMethods.forEach(executable -> {
                    out.println("@Override");
                    out.print(" public ");
                    out.print(executable.getSimpleName());
                    out.print("(");
                    out.print(parameterList(executable));
                    out.println(") {");

                    out.print("  Object[] args = ");
                    out.print(parameterArray(executable));
                    out.println(";");

                    out.print("  Class<?[] parameterTypes = ");
                    out.print(parameterTypeArray(executable));
                    out.println(";");

                    out.print("  String methodName = \"");
                    out.print(executable.getSimpleName());
                    out.println("\";");

                    out.println("  java.util.List handlers = new ArrayList<>();");
                    handlers(executable).forEach(handler -> {
                        out.printf("handlers.add(ApplicationContext.getInstance(%s.class))", handler.getClass());
                        out.println(";");
                    });

                    out.printf("  %s", executable.getReturnType());
                    out.println(" returnValue = null;");
                    out.println(" for (MethodHandlerRef handler : handlers) {");
                    out.printf("   returnValue = (%s.class) handler.invoke()", executable.getReturnType());
                    out.println(";");
                    out.println("  }");

                    out.println("  return returnValue;");
                    out.println("  }");
                });


                out.println("}");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String parameterList(ExecutableElement executable) {
        return executable.getParameters().stream()
                .map(param -> param.asType().toString() + " " + param.getSimpleName())
                .collect(Collectors.joining(", "));
    }

    private String parameterArray(ExecutableElement executable) {
        return "[" + executable.getParameters().stream()
                .map(param -> param.getSimpleName())
                .collect(Collectors.joining(", ")) + "]";
    }

    private String parameterTypeArray(ExecutableElement executable) {
        return "[" + executable.getParameters().stream()
                .map(param -> param.asType().toString() + ".class")
                .collect(Collectors.joining(", ")) + "]";
    }

    private List<MethodAdvice> handlers(ExecutableElement executable) {
        List<MethodAdvice> handlers = new ArrayList<>();
        ProcessorConfig.getMethodAnnotations().entrySet().forEach(e -> {
            if (executable.getAnnotation(e.getKey()) != null) {
                handlers.add(e.getValue());
            }
        });
        return handlers;
    }

    private void writeSingletonLoader(TypeElement e) {
        // TODO reuse code here instead of c&p
        log("processing %s", e.getQualifiedName());
        String simpleName = "Loader_" + randomString();
        String qualifiedName = PACKAGE + "." + simpleName;
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(qualifiedName).openOutputStream()))) {
            out.print("package ");
            out.print(PACKAGE);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import java.lang.reflect.*;");
            out.println("@SingletonLoader");
            out.print("public class ");
            out.print(simpleName);
            out.print(" extends ");
            out.print(SingletonLoaderBase.class.getName());
            out.println(" {");
            out.print("   public ");
            out.print(simpleName);
            out.println("() {");
            out.print("   super(\"");
            out.print(proxyQualifiedName(e));
            out.println("\");");
            out.println("  }");
            out.println(" }");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}
