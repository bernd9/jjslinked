package com.ejaf;

import java.lang.annotation.Annotation;

public interface ParameterProvider<A extends Annotation, C extends InvocationContext> {

    <T> T getParameter(ParameterContext<A, C> parameterContext, Class<T> type);

    boolean isSupportedParameterType(Class<?> c);
}
