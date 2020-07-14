package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


@RequiredArgsConstructor
public abstract class MethodAdvice {

    @Getter
    private final Class<? extends Annotation> annotationClass;

    public Object invoke(Object bean, String methodName, Class<?>[] types, Object[] args) {
        try {
            Method method = bean.getClass().getSuperclass().getDeclaredMethod(methodName, types);
            return invoke(bean, method, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Object invoke(Object bean, Method method, Object[] parameters);
}
