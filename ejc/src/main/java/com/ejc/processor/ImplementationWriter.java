package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.util.ReflectionUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ejc.util.JavaPoetUtils.parameterTypeListBlock;
import static com.ejc.util.ReflectionUtils.*;

@RequiredArgsConstructor
public class ImplementationWriter {
    private final String superClassQualifiedName;
    private final Map<String, List<TypeElement>> advices;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        String implName = superClassQualifiedName + "Impl";
        TypeSpec typeSpec = TypeSpec.classBuilder(getSimpleName(implName))
                .addAnnotation(createImplAnnotation())
                .addModifiers(Modifier.PUBLIC)
                .superclass(asTypeMirror(superClassQualifiedName))
                .addMethods(createImplMethods())
                .build();

        JavaFile javaFile = JavaFile.builder(getPackageName(implName), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
        javaFile.writeTo(Path.of("testxyz"));
    }

    private AnnotationSpec createImplAnnotation() {
        return AnnotationSpec.builder(Implementation.class)
                .addMember("forClass", String.format("\"%s\"", superClassQualifiedName))
                .build();
    }

    private List<MethodSpec> createImplMethods() {
        return getMethodsToOverride().map(this::createImplMethod).collect(Collectors.toList());
    }

    private Stream<ExecutableElement> getMethodsToOverride() {
        Set<String> overrideSignatures = advices.keySet();
        return asTypeElement(superClassQualifiedName).getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(e -> overrideSignatures.contains(signature(e)));
    }


    private MethodSpec createImplMethod(ExecutableElement orig) {
        String signature = signature(orig);
        return MethodSpec.overriding(orig)
                .addModifiers(Modifier.PUBLIC)
                .addCode(createAdviceListBlock(signature))
                .addCode(createMethodInstanceBlock(orig))
                .addCode(createMethodParameterBlock(orig))
                .addCode(createAdviceExecutionBlock())
                .addStatement(createReturnStatement(orig))
                .build();
    }


    private CodeBlock createAdviceListBlock(String signature) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .addStatement("$T<$T> advices = new $T<>()", List.class, InvocationHandler.class, ArrayList.class);
        advices.get(signature).forEach(advice -> builder.addStatement("advices.add($T.getInstance().getBean($L.class))", ApplicationContext.class, advice.asType()));
        return builder.build();
    }

    private CodeBlock createMethodInstanceBlock(ExecutableElement e) {
        return CodeBlock.builder()
                .addStatement("$T method", Method.class)
                .beginControlFlow("try")
                .add("method = getClass().getSuperclass().getDeclaredMethod(\"$L\",", e.getSimpleName())
                .add(parameterTypeListBlock(e))
                .addStatement(")")
                .nextControlFlow("catch ($T e)", NoSuchMethodException.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .addStatement("method.setAccessible(true)")
                .build();
    }

    private CodeBlock createMethodParameterBlock(ExecutableElement orig) {
        return CodeBlock.builder()
                .addStatement("Object[] args = new Object[]{}", ReflectionUtils.parameterNameList(orig))
                .build();
    }


    private CodeBlock createAdviceExecutionBlock() {
        return CodeBlock.builder()
                .addStatement("Object rv = null")
                .beginControlFlow("try")
                .beginControlFlow("for ($T advice : advices)", InvocationHandler.class)
                .addStatement("rv = advice.invoke(this, method, args)")
                .endControlFlow()
                .nextControlFlow("catch ($T e)", Throwable.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();
    }

    private CodeBlock createReturnStatement(ExecutableElement orig) {
        return CodeBlock.builder()
                .add("return ($T) rv", orig.getReturnType())
                .build();
    }

    private TypeMirror asTypeMirror(String classname) {
        return asTypeElement(classname).asType();
    }

    private TypeElement asTypeElement(String classname) {
        return processingEnvironment.getElementUtils().getTypeElement(classname);
    }

}