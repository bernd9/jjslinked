package com.ejc.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

import static com.squareup.javapoet.TypeSpec.classBuilder;

@Builder
class CustomAnnotationProviderWriter {

    private ProcessingEnvironment processingEnvironment;
    private String providerClassSimpleName;
    private String providerClassPackageName;
    private TypeElement annotationClass;

    void write() {
        TypeSpec typeSpec = classBuilder(providerClassSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor())
                .superclass(CustomSingletonAnnotationProvider.class)
                .build();

        JavaFile javaFile = JavaFile.builder(providerClassPackageName + "." + providerClassSimpleName, typeSpec).build();
        try {
            javaFile.writeTo(processingEnvironment.getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("setAnnotationClass(\"$T\")", annotationClass)
                .build();
    }


}
