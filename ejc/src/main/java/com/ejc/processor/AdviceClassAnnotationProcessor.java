package com.ejc.processor;

import com.ejc.AdviceClass;
import com.ejc.util.ReflectionUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

import static com.ejc.util.ReflectionUtils.getPackageName;
import static com.ejc.util.ReflectionUtils.getSimpleName;
import static com.squareup.javapoet.TypeSpec.classBuilder;

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
        AnnotationMirror mirror = ReflectionUtils.getAnnotationMirror(annotation, AdviceClass.class);
        String adviceClass = ReflectionUtils.getAnnotationValue(mirror, "value").getValue().toString().replace(".class", "");
        writeProcessor(annotation, adviceClass);
    }

    // TODO move to Writerclass
    private void writeProcessor(TypeElement annotation, String adviceClass) {
        String processorName = annotation.getQualifiedName() + "Processor";
        TypeSpec typeSpec = classBuilder(getSimpleName(processorName))
                .addAnnotation(autoService())
                .addMethod(constructor(annotation, adviceClass))
                .addOriginatingElement(annotation)
                .addModifiers(Modifier.PUBLIC)
                .superclass(AdviceAnnotationProcessorBase.class)
                .build();

        JavaFile javaFile = JavaFile.builder(getPackageName(processorName), typeSpec).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // TODO move to Writerclass
    private MethodSpec constructor(TypeElement annotation, String adviceClass) {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($T.class, $L.class)", annotation.asType(), adviceClass)
                .build();
    }

    // TODO move to Writerclass
    private AnnotationSpec autoService() {
        return AnnotationSpec.builder(AutoService.class)
                .addMember("value", "$T.class", Processor.class)
                .build();
    }


    protected void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

}
