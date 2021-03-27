package com.ejc.processor.advice;


import com.ejc.JoinPoint;
import com.ejc.MethodAdvice;

class TestHandler implements MethodAdvice {

    @Override
    public Object execute(Object var1, Object[] var2, JoinPoint var3) throws Throwable {
        return 43;
    }
}