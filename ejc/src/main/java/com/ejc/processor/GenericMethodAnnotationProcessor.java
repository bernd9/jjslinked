package com.ejc.processor;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
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

    private AnnotationMirror getAnnotationMirror(ExecutableElement method) {
        return Iterables.getOnlyElement(method.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(annotationClass.getName()))
                .collect(Collectors.toSet()));
    }

    private Map<String, String> getAnnotationValues(ExecutableElement method) {
        return getAnnotationMirror(method).getElementValues().entrySet().stream()
                .collect(Collectors.toMap(e -> getName(e.getKey()), e -> e.getValue().getValue().toString()));
    }

    private String getName(ExecutableElement e) {
        return e.getSimpleName().toString();
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

    private String parameterList(ExecutableElement method) {
        return method.getParameters().stream()
                .map(param -> param.asType().toString() + ".class") // TODO Generics ?
                .collect(Collectors.joining(", "));

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
