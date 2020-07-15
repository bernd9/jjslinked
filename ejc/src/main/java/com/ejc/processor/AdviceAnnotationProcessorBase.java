package com.ejc.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AdviceAnnotationProcessorBase<A extends Annotation> extends AbstractProcessor {

    @Getter
    private final Class<A> annotationClass;

    @Getter
    private final Class<? extends InvocationHandler> handlerClass;

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
        Collection<ExecutableElement> methods = roundEnv.getElementsAnnotatedWith(annotationClass)
                .stream().map(ExecutableElement.class::cast)
                .collect(Collectors.toSet());
        if (!methods.isEmpty()) {
            writeAdvice(methods);
        }
    }

    private void writeAdvice(Collection<ExecutableElement> methods) {
        AdviceWriter adviceWriter = new AdviceWriter(annotationClass, handlerClass, methods, processingEnv);
        try {
            adviceWriter.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

}
