package com.jjslinked.ast;

import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.ClientId;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.annotations.UserId;
import com.jjslinked.processor.codegen.java.JavaCodeGeneratorUtils;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class AstBuilder {

    static ClientClassNode classNode(TypeElement e) {
        return ClientClassNode.builder()
                .typeElement(e)
                .qualifiedName(e.getQualifiedName().toString())
                .packageName(JavaCodeGeneratorUtils.getPackageName(e.getQualifiedName().toString()))
                .simpleName(e.getSimpleName().toString())
                .qualifier(e.getAnnotation(Client.class).value())
                .instanceName(JavaCodeGeneratorUtils.firstToLowerCase(e.getSimpleName().toString()))
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
                .proxyName(JavaCodeGeneratorUtils.firstToUpperCase(e.getSimpleName() + "Proxy"))
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
                .characterSequence(isCharacterSequence(variableElement))
                .primitive(isPrimitive(variableElement))
                .primitiveWrapper(isPrimitiveWrapper(variableElement))
                .complex(isComplex(variableElement))
                .type(variableElement.asType().toString())
                .typeKind(variableElement.asType().getKind())
                .parameterType(parameterType(variableElement))
                .build();
    }

    private static ParameterType parameterType(VariableElement e) {
        if (isUserId(e)) return ParameterType.USER_ID;
        if (isClientId(e)) return ParameterType.CLIENT_ID;
        if (isCharacterSequence(e)) return ParameterType.CHARACTER_SEQUENCE;
        if (isPrimitive(e)) return ParameterType.PRIMITIVE;
        if (isPrimitiveWrapper(e)) return ParameterType.PRIMITIVE_WRAPPER;
        return ParameterType.COMPLEX;
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

    /*
    private static Class<?> type(VariableElement e) {
        TypeMirror type = e.asType();
        switch (type.getKind()) {
            case TypeKind.ARRAY:
                return TypeKind.ARRAY.
            case "long":
                return "Long.TYPE";
            case "byte":
                return "Byte.TYPE";
            case "float":
                return "Float.TYPE";
            case "double":
                return "Double.TYPE";
            case "char":
                return "Character.TYPE";
            default:
                return type;
        }
    }
    */

    private static boolean isCharacterSequence(VariableElement e) {
        try {
            return CharSequence.class.isAssignableFrom(Class.forName(e.asType().toString()));
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static boolean isPrimitive(VariableElement e) {
        return e.asType().getKind().isPrimitive();
    }

    private static boolean isPrimitiveWrapper(VariableElement e) {
        try {
            Class<?> c = Class.forName(e.asType().toString());
            if (Number.class.isAssignableFrom(c)) {
                return true;
            }
            if (Byte.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static boolean isComplex(VariableElement e) {
        return !isPrimitive(e) && !isCharacterSequence(e) && !isPrimitiveWrapper(e);
    }
}
