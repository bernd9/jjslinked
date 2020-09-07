package com.ejc.processor;

import com.ejc.ApplicationContextFactory;
import com.ejc.Value;
import com.ejc.api.context.ApplicationContextFactoryBase;
import com.ejc.api.context.ClassReference;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ejc.util.ReflectionUtils.getGenericType;

@Builder
public class ApplicationContextFactoryWriter {

    private Set<ExecutableElement> initMethods;
    private Set<VariableElement> singleValueDependencies;
    private Set<VariableElement> multiValueDependencies;
    private Set<VariableElement> configValues;
    private Set<TypeElement> singletons;
    private Map<TypeElement, TypeElement> implementations;
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
        addImplementations(constructorBuilder);
        addSingleValueDependencies(constructorBuilder);
        addMultiValueDependencies(constructorBuilder);
        addInitMethods(constructorBuilder);
        addConfigValues(constructorBuilder);
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

    void addSingletons(MethodSpec.Builder constructorBuilder) {
        singletons.forEach(type -> addSingleton(type, constructorBuilder));
    }

    void addSingleton(TypeElement type, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addBeanClass($L)", ref(type));
    }

    void addConfigValues(MethodSpec.Builder constructorBuilder) {
        configValues.forEach(type -> addConfigValue(type, constructorBuilder));
    }

    private void addConfigValue(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigValueField($L, \"$L\", $T.class\"$L\")", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), field.asType(), getValueKey(field));
    }

    private String getValueKey(VariableElement field) {
        return field.getAnnotation(Value.class).value();
    }

    void addImplementations(MethodSpec.Builder constructorBuilder) {
        implementations.forEach((base, impl) -> addImplementation(base, implementations.get(impl), constructorBuilder));
    }

    void addImplementation(TypeElement base, TypeElement impl, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addImplementation($L, $L)", ref(base), ref(impl));
    }

    void addSingleValueDependencies(MethodSpec.Builder constructorBuilder) {
        singleValueDependencies.forEach(field -> addSingleValueDependency(field, constructorBuilder));
    }

    void addSingleValueDependency(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addSingleValueDependency($L, \"$L\", $L)", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), ref(field.asType()));
    }

    void addMultiValueDependencies(MethodSpec.Builder constructorBuilder) {
        multiValueDependencies.forEach(field -> addMultiValueDependency(field, constructorBuilder));
    }

    void addMultiValueDependency(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addMultiValueDependency($L, \"$L\", $L.class, $L)", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), stripGenericType(field.asType()), ref(getGenericType(field)));
    }

    void addInitMethods(MethodSpec.Builder constructorBuilder) {
        initMethods.forEach(method -> addInitMethod(method, constructorBuilder));
    }

    void addInitMethod(ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addInitMethod($L, \"$L\")", ref((TypeElement) method.getEnclosingElement()), method.getSimpleName());

    }

    private static String ref(TypeElement e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e)
                .build().toString();
    }

    private static String ref(TypeMirror e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e)
                .build().toString();
    }

    private String stripGenericType(TypeMirror typeMirror) {
        return typeMirror.toString().replaceAll("<[^]]+>", "");
    }
}
