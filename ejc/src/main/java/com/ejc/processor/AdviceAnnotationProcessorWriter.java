package com.ejc.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

import static com.ejc.util.JavaModelUtils.getPackageName;
import static com.ejc.util.JavaModelUtils.getSimpleName;
import static com.squareup.javapoet.TypeSpec.classBuilder;

@Builder
class AdviceAnnotationProcessorWriter {

    private ProcessingEnvironment processingEnvironment;
    private TypeElement annotation;
    private String adviceClass;

    void write() {
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
            javaFile.writeTo(processingEnvironment.getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private MethodSpec constructor(TypeElement annotation, String adviceClass) {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($T.class, $L.class)", annotation.asType(), adviceClass)
                .build();
    }

    private AnnotationSpec autoService() {
        return AnnotationSpec.builder(AutoService.class)
                .addMember("value", "$T.class", Processor.class)
                .build();
    }

}
