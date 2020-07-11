package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@RequiredArgsConstructor
public abstract class MethodHandler<A extends Annotation> {

    @Getter
    @Setter
    private Class<A> annotationClass;


    public Object invoke(Object bean, String methodName, Class<?>[] types, Object[] args) {
        try {
            Method method = bean.getClass().getSuperclass().getDeclaredMethod(methodName, types);
            A annotation = method.getAnnotation(annotationClass);
            return invoke(bean, method, annotation, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract Object invoke(Object bean, Method method, A annotation, Object[] parameters);
}
