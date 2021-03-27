package com.ejc;

public interface MethodAdvice {

    Object execute(Object proxy, Object[] args, JoinPoint joinPoint) throws Throwable;
}
