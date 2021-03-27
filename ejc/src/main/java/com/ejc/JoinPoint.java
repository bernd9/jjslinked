package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class JoinPoint {

    public abstract Object proceed(Object proxy, Object[] args) throws Throwable;

    public abstract Method getMethod();

    public Object execute(Object proxy, Object[] args) {
        try {
            return proceed(proxy, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static JoinPoint prepare(List<MethodAdvice> methodAdvices, Method method) {
        Iterator<MethodAdvice> methodAdviceIterator = methodAdvices.iterator();
        if (!methodAdviceIterator.hasNext()) {
            return new MethodJoinPoint(method);
        }

        MethodAdviceJoinPoint joinPoint = new MethodAdviceJoinPoint(methodAdviceIterator.next());
        prepare(joinPoint, methodAdviceIterator, method);
        return joinPoint;
    }


    private static void prepare(MethodAdviceJoinPoint parent, Iterator<MethodAdvice> methodAdvices, Method method) {
        if (methodAdvices.hasNext()) {
            MethodAdviceJoinPoint joinPoint = new MethodAdviceJoinPoint(methodAdvices.next());
            parent.next = joinPoint;
            prepare(joinPoint, methodAdvices, method);
        } else {
            parent.next = new MethodJoinPoint(method);
        }
    }

    @RequiredArgsConstructor
    private static class MethodJoinPoint extends JoinPoint {

        @Getter
        private final Method method;

        @Override
        public Object proceed(Object proxy, Object[] args) throws Throwable {
            return method.invoke(proxy, args);
        }
    }

    @RequiredArgsConstructor
    private static class MethodAdviceJoinPoint extends JoinPoint {
        private final MethodAdvice methodAdvice;
        private JoinPoint next;

        @Override
        public Object proceed(Object proxy, Object[] args) throws Throwable {
            return methodAdvice.execute(proxy, args, next);
        }

        @Override
        public Method getMethod() {
            return next.getMethod();
        }

    }

}
