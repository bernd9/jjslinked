package com.ejc.processor;

import com.ejc.MethodAdvice;
import com.ejc.Singleton;

import java.lang.reflect.Method;

@Singleton
class TestMethodHandler extends MethodAdvice {

    TestMethodHandler() {
        super(TestAnnotation.class);
    }

    @Override
    public Object invoke(Object bean, Method method, Object[] parameters) {
        return null;
    }

}