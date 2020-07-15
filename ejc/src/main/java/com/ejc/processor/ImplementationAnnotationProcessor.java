package com.ejc.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

import static com.ejc.util.ReflectionUtils.getAnnotationMirrorOptional;
import static com.ejc.util.ReflectionUtils.getAnnotationValue;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.processor.Implementation"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ImplementationAnnotationProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated.singleton";

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
        roundEnv.getElementsAnnotatedWith(Implementation.class).stream()
                .map(TypeElement.class::cast)
                .forEach(this::writeLoader);
    }

    private String getSuperClassToOverride(TypeElement e) {
        return getAnnotationMirrorOptional(e, Implementation.class)
                .map(mirror -> getAnnotationValue(mirror, "forClass"))
                .map(AnnotationValue::getValue)
                .map(Object::toString).orElseThrow();
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
            out.printf("@ImplementationLoader(\"%s\")", getSuperClassToOverride(e));
            out.println();
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
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }
}
