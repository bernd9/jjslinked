package com.ejc.processor;

import com.ejc.AdviceClass;
import com.ejc.util.JavaModelUtils;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Writes an annotation-processor for each custom-aop-annotation.
 * These annotations are annotated with @{@link AdviceClass} and in this way,
 * the associated {@link java.lang.reflect.InvocationHandler} is given.
 * The new processor extends {@link AdviceAnnotationProcessorBase} and is
 * writing classes based on the handler without any extensions, but annotated
 * with @{@link com.ejc.Advice}
 */
@SupportedAnnotationTypes({"com.ejc.AdviceClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
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
                .forEach(this::writeProcessor);
    }

    private void writeProcessor(TypeElement annotation) {
        AnnotationMirror mirror = JavaModelUtils.getAnnotationMirror(annotation, AdviceClass.class);
        String adviceClass = JavaModelUtils.getAnnotationValue(mirror, "value").getValue().toString().replace(".class", "");
        AdviceAnnotationProcessorWriter annotationProcessorWriter = AdviceAnnotationProcessorWriter.builder()
                .adviceClass(adviceClass)
                .annotation(annotation)
                .processingEnvironment(processingEnv)
                .build();

        annotationProcessorWriter.write();
    }


    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }

}
