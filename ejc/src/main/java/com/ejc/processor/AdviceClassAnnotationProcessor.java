package com.ejc.processor;

import com.ejc.AdviceClass;
import com.ejc.util.ElementUtils;
import com.google.auto.common.AnnotationMirrors;
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

/**
 * Creates a method advice
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.AdviceClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceClassAnnotationProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processAnnotations(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(AdviceClass.class)
                .stream().map(TypeElement.class::cast)
                .forEach(this::processAnnotation);
    }

    private void processAnnotation(TypeElement annotation) {
        String methodAAdviceName = annotation.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(AdviceClass.class.getName()))
                .map(mirror -> AnnotationMirrors.getAnnotationValue(mirror, "value"))
                .map(AnnotationValue::getValue)
                .map(Object::toString)
                .findFirst().orElseThrow();
        writeMapping(annotation, methodAAdviceName);
    }

    private void writeMapping(TypeElement annotation, String methodAdviceClassName) {
        log("processing %s - %s", annotation, methodAdviceClassName);
        String packageName = ElementUtils.getPackageName(methodAdviceClassName);
        String simpleName = "Advice_" + randomString();
        String qualifiedName = packageName + "." + simpleName;
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(qualifiedName).openOutputStream()))) {
            //try (PrintWriter out = new PrintWriter(System.out)) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import com.ejc.*;");
            out.println("import java.lang.reflect.*;");
            out.printf("@Advice(%s.class)", annotation.getQualifiedName());
            out.println();
            out.printf("public class %s extends %s {", simpleName, methodAdviceClassName);
            out.println("}");
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
