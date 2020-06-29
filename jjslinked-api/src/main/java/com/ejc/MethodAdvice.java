package com.ejc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public interface MethodAdvice {

    Object invoke(Method method, Set<? extends Annotation> annotations, List<MethodParameter> parameters) throws Throwable;

    boolean invoke();

}
