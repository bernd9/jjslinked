package com.ejc.test;

import com.ejc.util.FieldUtils;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@RequiredArgsConstructor
class UnitTestContextBuilder {

    private final Object test;

    UnitTestContext createUnitTestContext() {
        return UnitTestContext.builder()
                .test(test)
                .testAnnotatedFields(testAnnotatedFields(test))
                //.testAnnotatedMethods(testAnnotatedMethods(test))
                .build();
    }

    private Map<Class<? extends Annotation>, Collection<Field>> testAnnotatedFields(Object test) {
        Map<Class<? extends Annotation>, Collection<Field>> annotatedFields = new HashMap<>();
        FieldUtils.getAllFields(test).forEach(field ->
                Arrays.stream(field.getAnnotations())
                        .filter(this::isUnitTestAnnotation)
                        .forEach(annotation -> annotatedFields.computeIfAbsent(annotation.annotationType(), a -> new ArrayList<>()).add(field)));
        return annotatedFields;
    }

    /*
    private Map<? extends Annotation, Collection<Method>> testAnnotatedMethods(Object test) {
        Map<Annotation, Collection<Method>> annotatedMethods = new HashMap<>();
        getMethods(test).forEach(method ->
                Arrays.stream(method.getAnnotations())
                        .filter(this::isUnitTestAnnotation)
                        .forEach(annotation -> annotatedMethods.computeIfAbsent(annotation, a -> new ArrayList<>()).add(method)));
        return annotatedMethods;
    }

     */

    private boolean isUnitTestAnnotation(Annotation annotation) {
        return annotation.annotationType().isAnnotationPresent(UnitTestAnnotation.class);
    }


    /*
    private Collection<Method> getMethods(Object o) {
        Map<String, Method> methods = new HashMap<>();
        getHierarchy(o.getClass()).stream()
                .map(Class::getDeclaredMethods)
                .map(Arrays::asList)
                .flatMap(List::stream)
                .filter(method -> !Modifier.isPrivate(method.getModifiers()))
                .peek(method -> method.setAccessible(true))
                .forEach(method -> methods.put(method.getName(), method));
        return methods.values();
    }

    private List<Class<?>> getHierarchy(Class<?> c) {
        List<Class<?>> classes = new ArrayList<>();
        while (c != null && c != Object.class) {
            classes.add(c);
            c = c.getSuperclass();
        }
        Collections.reverse(classes);
        return classes;
    }

     */

}
