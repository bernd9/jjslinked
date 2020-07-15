package com.ejc.processor;

import com.ejc.Singleton;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Singleton
class TestMethodHandler implements InvocationHandler {

    @Override
    public Object invoke(Object bean, Method method, Object[] parameters) {
        return null;
    }

}