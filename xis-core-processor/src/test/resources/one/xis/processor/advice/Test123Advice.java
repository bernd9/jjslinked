package one.xis.processor.advice;

import one.xis.Advice;
import one.xis.AdviceTarget;
import one.xis.JoinPoint;
import one.xis.MethodAdvice;

@Advice(annotation = TestAnnotation.class, targets = @AdviceTarget(declaringClass = AdviceTestBean.class, signature = "xyz(java.lang.String)"))
class Test123Advice implements MethodAdvice {

    @Override
    public Object execute(Object var1, Object[] var2, JoinPoint var3) throws Throwable {
        return 42;
    }
}