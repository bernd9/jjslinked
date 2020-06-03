package com.jjslinked.ast;

import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.ClientId;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.annotations.UserId;
import com.jjslinked.parameters.ParameterProvider;
import com.jjslinked.parameters.UserIdParameterProvider;
import com.jjslinked.processor.codegen.JavaSourceUtils;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class AstBuilder {

    static ClassNode classNode(TypeElement e) {
        return ClassNode.builder()
                .typeElement(e)
                .qualifiedName(e.getQualifiedName().toString())
                .packageName(JavaSourceUtils.getPackageName(e.getQualifiedName().toString()))
                .simpleName(e.getSimpleName().toString())
                .qualifier(e.getAnnotation(Client.class).value())
                .instanceName(JavaSourceUtils.firstToLowerCase(e.getSimpleName().toString()))
                .methods(methodNodes(e))
                .build();
    }

    private static List<MethodNode> methodNodes(TypeElement e) {
        return e.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(AstBuilder::isLinkedMethod)
                .map(ExecutableElement.class::cast)
                .map(AstBuilder::methodNode)
                .collect(Collectors.toList());
    }

    private static MethodNode methodNode(ExecutableElement e) {
        return MethodNode.builder()
                .executableElement(e)
                .qualifier(e.getAnnotation(LinkedMethod.class).value())
                .proxyName(JavaSourceUtils.firstToUpperCase(e.getSimpleName() + "Proxy"))
                .proxyInstanceName(e.getSimpleName() + "Proxy")
                .name(e.getSimpleName().toString())
                .parameters(parameterNodes(e))
                .abstractMethod(e.getModifiers().contains(Modifier.ABSTRACT))
                .invokationType(e.getModifiers().contains(Modifier.ABSTRACT) ? InvokationType.SERVER : InvokationType.CLIENT)
                .returnType(e.getReturnType().toString())
                .returnTypeKind(e.getReturnType().getKind())
                .build();
    }

    private static List<ParameterNode> parameterNodes(ExecutableElement e) {
        return e.getParameters().stream()
                .map(AstBuilder::parameterNode)
                .collect(Collectors.toList());
    }

    private static ParameterNode parameterNode(VariableElement variableElement) {
        return ParameterNode.builder()
                .name(variableElement.getSimpleName())
                .clientId(isClientId(variableElement))
                .userId(isUserId(variableElement))
                .type(variableElement.asType().toString())
                .typeKind(variableElement.asType().getKind())
                .build();
    }

    private static ParameterProvider parameterProvider(VariableElement variableElement) {
        if (isUserId(variableElement)) {
            return new UserIdParameterProvider();
        }
        if (isClientId(variableElement)) {
            return new UserIdParameterProvider();
        }
        // TODO
        return null;
    }

    private static boolean isLinkedMethod(ExecutableElement e) {
        return isAnnotatedWith(e, LinkedMethod.class);
    }

    private static boolean isUserId(Element e) {
        return isAnnotatedWith(e, UserId.class);
    }

    private static boolean isClientId(Element e) {
        return isAnnotatedWith(e, ClientId.class);
    }

    private static boolean isAnnotatedWith(Element e, Class<? extends Annotation> annotation) {
        return e.getAnnotation(annotation) != null;
    }
}
