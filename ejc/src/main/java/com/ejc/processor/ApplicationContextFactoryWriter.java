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

import static com.ejc.util.ReflectionUtils.getGenericType;

@Builder
public class ApplicationContextFactoryWriter {

    private Set<ExecutableElement> initMethodsSingleton;
    private Set<ExecutableElement> initMethodsConfiguration;
    private Set<ExecutableElement> loadBeanMethods;
    private Set<VariableElement> singleValueDependencies;
    private Set<VariableElement> multiValueDependencies;
    private Set<VariableElement> configFieldsSingleton;
    private Set<VariableElement> configFieldsConfiguration;
    private Set<TypeElement> singletons;
    private Set<TypeElement> configurations;
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
        addConfigurations(constructorBuilder);
        addSingletons(constructorBuilder);
        addImplementations(constructorBuilder);
        addSingleValueDependencies(constructorBuilder);
        addMultiValueDependencies(constructorBuilder);
        addInitMethodsConfigurations(constructorBuilder);
        addInitMethodsSingleton(constructorBuilder);
        addConfigFieldsSingleton(constructorBuilder);
        addConfigFieldsConfiguration(constructorBuilder);
        addLoadBeandMethods(constructorBuilder);
        return constructorBuilder.build();
    }

    private void addOriginatingElements(TypeSpec.Builder builder) {
        Set<Element> originatingElements = new HashSet<>();
        originatingElements.addAll(singletons);
        originatingElements.addAll(configurations);
        originatingElements.forEach(builder::addOriginatingElement);
    }

    private void addSingletons(MethodSpec.Builder constructorBuilder) {
        singletons.forEach(type -> addSingleton(type, constructorBuilder));
    }

    private void addConfigurations(MethodSpec.Builder constructorBuilder) {
        configurations.forEach(type -> addConfiguration(type, constructorBuilder));
    }

    private void addSingleton(TypeElement type, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addBeanClass($L)", ref(type));
    }

    private void addConfiguration(TypeElement type, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigurationClass($L)", ref(type));
    }

    private void addConfigFieldsSingleton(MethodSpec.Builder constructorBuilder) {
        configFieldsSingleton.forEach(type -> addConfigFieldSingleton(type, constructorBuilder));
    }

    private void addConfigFieldSingleton(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigValueFieldInSingleton($L, \"$L\", $T.class, \"$L\")", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), field.asType(), getValueKey(field));
    }

    private void addConfigFieldsConfiguration(MethodSpec.Builder constructorBuilder) {
        configFieldsConfiguration.forEach(type -> addConfigFieldConfiguration(type, constructorBuilder));
    }

    private void addConfigFieldConfiguration(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigValueFieldInConfiguration($L, \"$L\", $T.class, \"$L\")", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), field.asType(), getValueKey(field));
    }

    private String getValueKey(VariableElement field) {
        return field.getAnnotation(Value.class).value();
    }

    void addImplementations(MethodSpec.Builder constructorBuilder) {
        implementations.forEach((base, impl) -> addImplementation(base, implementations.get(impl), constructorBuilder));
    }

    private void addImplementation(TypeElement base, TypeElement impl, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addImplementation($L, $L)", ref(base), ref(impl));
    }

    private void addSingleValueDependencies(MethodSpec.Builder constructorBuilder) {
        singleValueDependencies.forEach(field -> addSingleValueDependency(field, constructorBuilder));
    }

    private void addSingleValueDependency(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addSingleValueDependency($L, \"$L\", $L)", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), ref(field.asType()));
    }

    private void addMultiValueDependencies(MethodSpec.Builder constructorBuilder) {
        multiValueDependencies.forEach(field -> addMultiValueDependency(field, constructorBuilder));
    }

    private void addMultiValueDependency(VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addMultiValueDependency($L, \"$L\", $L.class, $L)", ref((TypeElement) field.getEnclosingElement()), field.getSimpleName(), stripGenericType(field.asType()), ref(getGenericType(field)));
    }

    private void addInitMethodsSingleton(MethodSpec.Builder constructorBuilder) {
        initMethodsSingleton.forEach(method -> addInitMethodSingleton(method, constructorBuilder));
    }

    private void addInitMethodsConfigurations(MethodSpec.Builder constructorBuilder) {
        initMethodsConfiguration.forEach(method -> addInitMethodConfiguration(method, constructorBuilder));
    }

    private void addInitMethodSingleton(ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addInitMethodForSingleton($L, \"$L\")", ref((TypeElement) method.getEnclosingElement()), method.getSimpleName());
    }

    private void addInitMethodConfiguration(ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addInitMethodForConfiguration($L, \"$L\")", ref((TypeElement) method.getEnclosingElement()), method.getSimpleName());
    }

    private void addLoadBeandMethods(MethodSpec.Builder constructorBuilder) {
        loadBeanMethods.forEach(method -> addLoadBeanMethod(method, constructorBuilder));
    }

    private void addLoadBeanMethod(ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addLoadBeanMethod($L, \"$L\")", ref((TypeElement) method.getEnclosingElement()), method.getSimpleName());
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
