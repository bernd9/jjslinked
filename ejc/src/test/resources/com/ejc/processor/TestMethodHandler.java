package com.ejc.processor;

import com.ejc.MethodHandler;
import com.ejc.Singleton;

import java.lang.reflect.Method;

@Singleton
abstract class TestMethodHandler extends MethodHandler<TestAnnotation> {

    public Object invoke(Object bean, Method method, TestAnnotation annotation, Object[] parameters) {
        return null;
    }

}