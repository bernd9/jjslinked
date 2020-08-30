package com.ejc.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ejc.util.ReflectionUtils.getGenericType;

@Builder
public class ApplicationContextFactoryWriter {

    private Set<ExecutableElement> initMethods;
    private Set<VariableElement> singleValueDependencies;
    private Set<VariableElement> multiValueDependencies;
    private Set<TypeElement> singletons;
    private String packageName;
    private ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor())
                .superclass(ApplicationContextFactoryBase.class);
        addOriginatingElements(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        addSingletons(constructorBuilder);
        addSingleValueDependencies(constructorBuilder);
        addMultiValueDependencies(constructorBuilder);
        addInitMethods(constructorBuilder);
        return constructorBuilder.build();
    }

    private void addOriginatingElements(TypeSpec.Builder builder) {
        Set<Element> originatingElements = new HashSet<>();
        originatingElements.addAll(singletons);
        originatingElements.addAll(singleValueDependencies.stream().map(VariableElement::getEnclosingElement).collect(Collectors.toSet()));
        originatingElements.addAll(multiValueDependencies.stream().map(VariableElement::getEnclosingElement).collect(Collectors.toSet()));
        originatingElements.addAll(initMethods.stream().map(ExecutableElement::getEnclosingElement).collect(Collectors.toSet()));
        originatingElements.forEach(builder::addOriginatingElement);
    }

    void addSingletons(MethodSpec.Builder contructorBuilder) {
        singletons.forEach(type -> addSingleton(type, contructorBuilder));
    }

    void addSingleton(TypeElement type, MethodSpec.Builder contructorBuilder) {
        contructorBuilder.addStatement("addBeanClass($T.class)", type);
    }

    void addSingleValueDependencies(MethodSpec.Builder contructorBuilder) {
        singleValueDependencies.forEach(field -> addSingleValueDependency(field, contructorBuilder));
    }

    void addSingleValueDependency(VariableElement field, MethodSpec.Builder contructorBuilder) {
        contructorBuilder.addStatement("addSingleValueDependency($T.class, \"$L\", $T.class)", field.getEnclosingElement(), field.getSimpleName(), field.asType());
    }

    void addMultiValueDependencies(MethodSpec.Builder contructorBuilder) {
        multiValueDependencies.forEach(field -> addMultiValueDependency(field, contructorBuilder));
    }

    void addMultiValueDependency(VariableElement field, MethodSpec.Builder contructorBuilder) {
        contructorBuilder.addStatement("addMultiValueDependency($T.class, \"$L\", $T.class)", field.getEnclosingElement(), field.getSimpleName(), field.asType(), getGenericType(field));
    }

    void addInitMethods(MethodSpec.Builder contructorBuilder) {
        initMethods.forEach(method -> addInitMethod(method, contructorBuilder));
    }

    void addInitMethod(ExecutableElement method, MethodSpec.Builder contructorBuilder) {
        contructorBuilder.addStatement("addInitMethod($T.class, \"$L\")", method.getEnclosingElement(), method.getSimpleName());

    }
}
