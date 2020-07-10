package com.ejc.processor;

import com.ejc.SystemProperty;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.SystemProperty"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SystemPropertyAnnotationProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated.sysprop";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInjects(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processInjects(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SystemProperty.class).stream()
                .map(VariableElement.class::cast)
                .forEach(this::writeInjector);
    }


    private void writeInjector(VariableElement e) {
        log("processing %s", e);
        String injectorSimpleName = "PropertyInjector_" + randomString();
        String injectorQualifiedName = PACKAGE + "." + injectorSimpleName;
        String fieldDeclaringClass = ((TypeElement) e.getEnclosingElement()).getQualifiedName().toString();
        String fieldName = e.getSimpleName().toString();
        String propertyName = propertyName(e);
        String defaultValue = defaultValue(e);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(injectorQualifiedName).openOutputStream()))) {
            out.print("package ");
            out.print(PACKAGE);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import java.lang.reflect.*;");
            out.println("@SystemPropertyInjector");
            out.print("public class ");
            out.print(injectorSimpleName);
            out.print(" extends ");
            out.print(SystemPropertyInjectorBase.class.getName());
            out.println(" {");
            out.print(" public ");
            out.print(injectorSimpleName);
            out.println("() {");
            out.print("   super(");
            out.print("\"");
            out.print(fieldDeclaringClass);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(fieldName);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(propertyName);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(defaultValue);
            out.print("\"");
            out.println(");");
            out.println("  }");
            out.println(" }");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String propertyName(VariableElement e) {
        return e.getAnnotation(SystemProperty.class).name();
    }

    private String defaultValue(VariableElement e) {
        return e.getAnnotation(SystemProperty.class).defaultValue();
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
