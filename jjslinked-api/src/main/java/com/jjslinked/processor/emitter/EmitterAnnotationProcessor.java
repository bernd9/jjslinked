package com.jjslinked.processor.emitter;

import com.google.auto.service.AutoService;
import com.jjslinked.model.ClassModel;
import com.jjslinked.model.MethodModel;
import com.jjslinked.processor.util.AnnotationUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.Emitter")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EmitterAnnotationProcessor extends AbstractProcessor {

    private final Map<TypeElement, List<ExecutableElement>> emitters = new HashMap<>();
    private final EmitterImplOfInterfaceTemplate template = new EmitterImplOfInterfaceTemplate();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        emitters.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                emitters.entrySet().forEach(e -> this.createImplementation(e.getKey(), e.getValue()));
            } else {
                annotations.stream()
                        .map(roundEnv::getElementsAnnotatedWith)
                        .flatMap(Set::stream)
                        .map(ExecutableElement.class::cast)
                        .forEach(this::mapEmitterMethod);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void mapEmitterMethod(ExecutableElement e) {
        emitters.computeIfAbsent((TypeElement) e.getEnclosingElement(), method -> new LinkedList<>())
                .add(e);
    }

    private void createImplementation(TypeElement type, List<ExecutableElement> emitterMethods) {
        ClassModel classModel = ClassModel.from(type.getQualifiedName().toString(), AnnotationUtil.getAnnotations(type));
        List<MethodModel> emitters = emitterMethods.stream().map(MethodModel::fromElement).collect(Collectors.toList());
        EmitterImplModel model = EmitterImplModel.builder()
                .classModel(classModel)
                .emitters(emitters)
                .build();
        template.write(model, processingEnv.getFiler());
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}