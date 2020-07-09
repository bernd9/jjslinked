package com.ejc.processor;

import com.ejc.Init;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Init"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class InitAnnotationProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated.init";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInits(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processInits(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Init.class).stream()
                .map(ExecutableElement.class::cast)
                .peek(this::validateNoParameters)
                .forEach(this::writeIntializer);
    }

    private void validateNoParameters(ExecutableElement element) {
        if (!element.getParameters().isEmpty()) {
            throw new IllegalStateException(element + " must have no parameters");
        }
    }


    private void writeIntializer(ExecutableElement e) {
        log("processing %s", e);
        String initializerSimpleName = "Initializer_" + randomString();
        String initializerQualifiedName = PACKAGE + "." + initializerSimpleName;
        String ownerClass = ((TypeElement) e.getEnclosingElement()).getQualifiedName().toString();
        String methodName = e.getSimpleName().toString();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(initializerQualifiedName).openOutputStream()))) {
            out.print("package ");
            out.print(PACKAGE);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import java.lang.reflect.*;");
            out.println("@Initializer");
            out.print("public class ");
            out.print(initializerSimpleName);
            out.print(" extends ");
            out.print(InitializerBase.class.getName());
            out.println(" {");
            out.print(" public ");
            out.print(initializerSimpleName);
            out.println("() {");
            out.print("   super(");
            out.print("\"");
            out.print(ownerClass);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(methodName);
            out.print("\"");
            out.println(");");
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
