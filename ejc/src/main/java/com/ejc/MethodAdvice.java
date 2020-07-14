package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@RequiredArgsConstructor
public abstract class MethodAdvice<A extends Annotation> {

    @Getter
    private final Class<A> annotationClass;

    public Object invoke(Object bean, String methodName, Class<?>[] types, Object[] args) {
        try {
            Method method = bean.getClass().getSuperclass().getDeclaredMethod(methodName, types);
            A annotation = method.getAnnotation(annotationClass);
            return invoke(bean, method, annotation, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Object invoke(Object bean, Method method, A annotation, Object[] parameters);
}
