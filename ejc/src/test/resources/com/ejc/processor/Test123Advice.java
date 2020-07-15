package com.ejc.processor;

import com.ejc.Advice;
import com.ejc.AdviceTarget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Advice(annotation = TestAnnotation.class, targets = @AdviceTarget(declaringClass = AdviceTestBean.class, signature = "xyz(java.lang.String)"))
class Test123Advice implements InvocationHandler {

    public Object invoke(Object bean, Method method, Object[] parameters) {
        return 42;
    }
}