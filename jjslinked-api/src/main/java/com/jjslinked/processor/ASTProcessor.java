package com.jjslinked.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.jjslinked.annotations.Client")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ASTProcessor extends AbstractProcessor {

    static int counter;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "ASTProcessor: counter=" +counter++);
        annotations.stream().flatMap(annotation -> roundEnvironment.getElementsAnnotatedWith(annotation).stream())
                .map(TypeElement.class::cast).forEach(this::processClientClass);
        return false;
    }

    private void processClientClass(TypeElement clientElement) {

    }
}
