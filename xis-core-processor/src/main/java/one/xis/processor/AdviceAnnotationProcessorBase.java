package one.xis.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.Advice;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Baseclass for generated annotation-processors, that will write subclasses of a handler
 * without any extension, but annotated with @{@link Advice}.
 * This processing is checking for annotated target-methods and also includes methods of
 * of annotated types.
 *
 * @param <A>
 */
@RequiredArgsConstructor
public class AdviceAnnotationProcessorBase<A extends Annotation> extends AbstractProcessor {

    @Getter
    private final Class<A> annotationClass;

    @Getter
    private final Class<? extends InvocationHandler> handlerClass;

    private final int priority;

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
        Set<ExecutableElement> methods = Stream.concat(processAnnotatedMethod(roundEnv), processAnnotatedType(roundEnv))
                .filter(m -> !m.getModifiers().contains(Modifier.FINAL))
                .filter(m -> !m.getModifiers().contains(Modifier.PRIVATE))
                .collect(Collectors.toSet());
        if (!methods.isEmpty()) {
            writeAdvice(methods);
        }
    }

    private Stream<ExecutableElement> processAnnotatedMethod(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(annotationClass)
                .stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast);
    }

    private Stream<ExecutableElement> processAnnotatedType(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(annotationClass)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .map(TypeElement::getEnclosedElements)
                .flatMap(List::stream)
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast);
    }

    private void writeAdvice(Collection<ExecutableElement> methods) {
        AdviceWriter adviceWriter = new AdviceWriter(annotationClass, handlerClass, methods, processingEnv, priority);
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
