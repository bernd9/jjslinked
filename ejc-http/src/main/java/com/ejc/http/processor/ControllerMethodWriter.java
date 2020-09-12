package com.ejc.http.processor;

import com.ejc.Singleton;
import com.ejc.http.api.controller.ControllerMethod;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.Collection;

@Builder
class ControllerMethodWriter {
    private String simpleClassName;
    private String packageName;
    private Collection<VariableElement> parameterTypes;
    private String httpMethod;
    private TypeElement controller;
    private String methodName;
    private ProcessingEnvironment processingEnvironment;


    void write() throws IOException {
        TypeSpec typeSpec = TypeSpec.classBuilder(simpleClassName).addAnnotation(Singleton.class)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor())
                .superclass(ControllerMethod.class)
                .addOriginatingElement(controller)
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());

    }

    private MethodSpec constructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("setMethodName(\"$L\")", methodName)
                .addStatement("setHttpMethod(\"$L\")", httpMethod);
        // TODO PathVariable-Annotation ier verwerten
        parameterTypes.forEach(type -> builder.addStatement("addParameterType($T.class)", type));

        return builder.build();
    }

}
