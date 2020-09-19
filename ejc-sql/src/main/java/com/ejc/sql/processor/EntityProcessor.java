package com.ejc.sql.processor;

import com.ejc.sql.Entity;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Entity.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("entity-processing ");
        if (!roundEnv.processingOver()) {

        }
        return true;
    }

    private String randomClassName() {
        return "Invoker_" + UUID.randomUUID().toString().replace("-", "");
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }


}