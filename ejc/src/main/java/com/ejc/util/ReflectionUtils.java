package com.ejc.util;

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

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

    // TODO share these methods with GenericMethodAnnotationProcessor:
    public static String signature(ExecutableElement method) {
        return new StringBuilder(method.getSimpleName())
                .append("(")
                .append(method.getParameters().stream()
                        .map(VariableElement::asType)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .append(")")
                .toString();
    }

    /**
     * For :
     * <pre>
     *     void method1(int x, String str, Integer[] numbers)
     * </pre>
     * <p>
     * return-value would be "x,str,numbers"
     *
     * @param method
     * @return
     */
    public static String parameterNameList(ExecutableElement method) {
        return method.getParameters().stream()
                .map(VariableElement::getSimpleName)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public static AnnotationMirror getAnnotationMirror(Element annotated, Class<? extends Annotation> annotationClass) {
        return Iterables.getOnlyElement(annotated.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(annotationClass.getName()))
                .collect(Collectors.toSet()));
    }

    public static Map<String, String> getAnnotationValues(AnnotationMirror annotationMirror) {
        Map<String, String> map = new HashMap<>();
        annotationMirror.getElementValues().forEach((executable, value) -> map.put(executable.getSimpleName().toString(), value.getValue().toString()));
        return map;
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
