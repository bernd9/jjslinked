package com.ejaf;

import java.lang.annotation.Annotation;

public interface ParameterProvider<A extends Annotation, C extends InvocationContext> {

    <T> T getParameter(ParameterContext<A> parameterContext, C invocationContext, Class<T> type);

    default boolean isSupportedType(Class<?> c) {
        return true;
    }
}
