package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public abstract class MethodHandler<A extends Annotation> {

    private final Class<A> annotationClass;

    private MethodHandler next;

    public Object invoke(Object bean, String methodName, Class<?>[] types, Object[] args) {
        try {
            Method method = bean.getClass().getSuperclass().getDeclaredMethod(methodName, types);
            A annotation = method.getAnnotation(annotationClass);
            Object rv = invoke(bean, method, annotation, args);
            if (next == null) {
                return rv;
            }
            return next.invoke(bean, methodName, types, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MethodHandler append(MethodHandler methodHandler) {
        next = methodHandler;
        return methodHandler;
    }


    abstract Object invoke(Object bean, Method method, A annotation, Object[] parameters);
}
