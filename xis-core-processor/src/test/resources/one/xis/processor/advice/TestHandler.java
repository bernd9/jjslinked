package one.xis.processor.advice;


import one.xis.JoinPoint;
import one.xis.MethodAdvice;

class TestHandler implements MethodAdvice {

    @Override
    public Object execute(Object var1, Object[] var2, JoinPoint var3) throws Throwable {
        return 43;
    }
}