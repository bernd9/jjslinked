package com.ejc.processor;

import com.ejc.Singleton;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SingletonAnnotationProcessor extends AbstractProcessor {

    static final String PACKAGE = "com.ejc.generated.singleton";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ProcessorConfig.getSingletonAnnotations().stream().map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processSingletons(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processSingletons(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .map(TypeElement.class::cast)
                .forEach(this::writeLoader);
    }


    private void writeLoader(TypeElement e) {
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
            out.print(e.getQualifiedName());
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
