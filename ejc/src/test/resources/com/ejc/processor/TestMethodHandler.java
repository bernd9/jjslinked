package com.ejc.processor;

import com.ejc.MethodAdvice;
import com.ejc.Singleton;

import java.lang.reflect.Method;

@Singleton
abstract class TestMethodHandler extends MethodAdvice<TestAnnotation> {

    TestMethodHandler() {
        super(TestAnnotation.class);
    }

    @Override
    public Object invoke(Object bean, Method method, TestAnnotation annotation, Object[] parameters) {
        return null;
    }

}