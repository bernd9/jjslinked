package com.ejc.util;

import com.google.common.collect.Iterables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.*;
import javax.lang.model.util.SimpleAnnotationValueVisitor9;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {


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

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String name) {
        AnnotationValue value = Iterables.getOnlyElement(annotationMirror.getElementValues().entrySet().stream()
                .filter(e -> e.getKey().getSimpleName().toString().equals(name))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
        Object o = value.accept(new ValueVisitor<>(), null);
        return value;
    }

    private static class ValueVisitor<R> extends SimpleAnnotationValueVisitor9<R, Class<?>> {

        @Override
        public R visitArray(List<? extends AnnotationValue> vals, Class<?> aClass) {

            return super.visitArray(vals, aClass);
        }
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

}
