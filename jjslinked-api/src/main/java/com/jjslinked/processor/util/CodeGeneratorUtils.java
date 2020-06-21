package com.jjslinked.processor.util;

import com.jjslinked.annotations.LinkedMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeGeneratorUtils {

    public static String firstToLowerCase(String s) {
        if (s == null || s.length() == 0)
            return "";
        return new StringBuilder().append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String firstToUpperCase(String s) {
        if (s == null || s.length() == 0)
            return "";
        return new StringBuilder().append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String getPackageName(Name qualifiedName) {
        return getPackageName(qualifiedName.toString());
    }

    public static String getPackageName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        return qualifiedName.substring(0, index);
    }

    public static String getSimpleName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        if (index == -1) {
            return qualifiedName;
        }
        return qualifiedName.substring(index + 1);
    }

    public static String providerClassName(VariableElement e) {
        return providerClassName(e.getSimpleName().toString());
    }

    public static String providerClassName(String variableName) {
        return CodeGeneratorUtils.firstToUpperCase(variableName) + "Provider";
    }

    public static String invocationClassName(ExecutableElement e) {
        return CodeGeneratorUtils.firstToUpperCase(e.getSimpleName().toString()) + "Invoker";
    }

    public static boolean isLinkedMethod(ExecutableElement executableElement) {
        return executableElement.getAnnotation(LinkedMethod.class) != null;
    }

    public static boolean isImplemented(ExecutableElement executableElement) { //TODO Geht das auch bein interface so ?
        return !isAbstract(executableElement);
    }

    public static boolean isAbstract(ExecutableElement executableElement) { //TODO Geht das auch bein interface so ?
        return executableElement.getModifiers().contains(Modifier.ABSTRACT);
    }

    public static String converterClass(TypeKind typeKind) {
        return null;// TODO
    }

    public static boolean isPrimitiveWrapper(String c) {
        try {
            //TODO make it faster by preset all classes
            return isPrimitiveWrapper(Class.forName(c));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isPrimitiveWrapper(Class<?> c) {
        return c.equals(Character.class) || Number.class.isAssignableFrom(c);
    }

    public static boolean isCharSequence(String parameterType) {
        try {
            //TODO make it faster by preset all classes
            return CharSequence.class.isAssignableFrom(Class.forName(parameterType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
