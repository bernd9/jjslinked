package com.ejc.processor;

import com.ejc.javapoet.JavaWriter;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Optional;

class CustomAnnotationProviderWriter extends JavaWriter {

    private TypeElement annotationClass;

    @Builder
    public CustomAnnotationProviderWriter(TypeElement annotationClass, String providerClassSimpleName, String providerClassPackageName, ProcessingEnvironment processingEnvironment) {
        super(providerClassSimpleName, Optional.of(providerClassPackageName), Optional.of(CustomSingletonAnnotationProvider.class), processingEnvironment, Collections.emptySet());
        this.annotationClass = annotationClass;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addModifiers(Modifier.PUBLIC)
                .addStatement("setAnnotationClass(\"$L\")", annotationClass.getQualifiedName())
                .build();
    }


}
