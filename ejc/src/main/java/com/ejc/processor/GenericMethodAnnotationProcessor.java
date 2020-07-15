package com.ejc.processor;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
abstract class GenericMethodAnnotationProcessor<A extends Annotation> extends AbstractProcessor {

    private final Class<A> annotationClass;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(annotationClass.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

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
        roundEnv.getElementsAnnotatedWith(annotationClass)
                .stream().map(ExecutableElement.class::cast)
                .forEach(this::processMethod);
    }


    private void processMethod(ExecutableElement method) {
        processMethod(method, adviceAnnotation(method));
    }

    private String adviceAnnotation(ExecutableElement method) {
        return new StringBuilder("@com.ejc.Advice(")
                .append(String.format("annotation=%s.class", annotationClass.getName()))
                .append(String.format(", declaringClass=%s.class", method.getEnclosingElement().asType()))
                .append(String.format(", signature=\"%s\")", signature(method)))
                .toString();
    }

    private String signature(ExecutableElement method) {
        return new StringBuilder(method.getSimpleName())
                .append("(")
                .append(method.getParameters().stream()
                        .map(VariableElement::asType)
                        .map(Object::toString)
                        // TODO Generics ?
                        .collect(Collectors.joining(", ")))
                .append(")")
                .toString();
    }

    protected abstract void processMethod(ExecutableElement method, String adviceAnnotation);

    protected String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

}
