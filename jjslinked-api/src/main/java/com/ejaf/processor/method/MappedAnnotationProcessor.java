package com.ejaf.processor.method;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

//@AutoService(Processor.class)
@SupportedAnnotationTypes("com.ejaf.ListenerMethod")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class MappedAnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                //annotatedMethods.entrySet().stream().forEach(e -> processParameters(e.getKey(), e.getValue()));
            } else {

            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}