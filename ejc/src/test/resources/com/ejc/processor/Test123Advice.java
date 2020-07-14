package com.ejc.processor;

import com.ejc.Advice;
import com.ejc.MethodAdvice;

import java.lang.reflect.Method;

@Advice(annotation = TestAnnotation.class, declaringClass = AdviceTestBean.class, signature = "xyz(java.lang.String)")
class Test123Advice extends MethodAdvice {

    Test123Advice() {
        super(TestAnnotation.class);
    }

    public Object invoke(Object bean, Method method, Object[] parameters) {
        return 42;
    }
}