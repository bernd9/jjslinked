package com.ejc.processor.advice;

import com.ejc.Advice;
import com.ejc.AdviceTarget;
import com.ejc.JoinPoint;
import com.ejc.MethodAdvice;

@Advice(annotation = TestAnnotation.class, targets = @AdviceTarget(declaringClass = AdviceTestBean.class, signature = "xyz(java.lang.String)"))
class Test123Advice implements MethodAdvice {

    @Override
    public Object execute(Object var1, Object[] var2, JoinPoint var3) throws Throwable {
        return 42;
    }
}