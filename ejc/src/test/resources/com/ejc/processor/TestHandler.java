package com.ejc.processor;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class TestHandler implements InvocationHandler {

    public Object invoke(Object bean, Method method, Object[] parameters) {
        return 43;
    }
}