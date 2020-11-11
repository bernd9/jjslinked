package com.ejc.processor;

import com.ejc.util.ProcessorLogger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProcessorBase extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingOver();
        } else {
            QueryResult result = new QueryResult();
            getSupportedAnnotationTypes().stream()
                    .map(name -> processingEnv.getElementUtils().getTypeElement(name))
                    .forEach(annotation -> process(annotation, roundEnv, result));
            process(result);
        }
        return false;
    }

    protected void process(TypeElement annotationClass, RoundEnvironment roundEnvironment, QueryResult result) {
        var name = annotationClass.getQualifiedName().toString();
        result.computeIfAbsent(name, n -> new HashSet<>())
                .addAll(roundEnvironment.getElementsAnnotatedWith(annotationClass).stream().collect(Collectors.toSet()));
    }


    protected abstract void process(QueryResult result);

    protected void processingOver() {

    }


    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }
}
