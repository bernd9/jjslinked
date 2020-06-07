package com.ejaf;

import java.lang.annotation.Annotation;

public interface ParameterProvider<A extends Annotation, C extends InvocationContext> {

    <T> T getParameter(String parameterName, A annotation, C context, Class<T> type);

    default boolean isSupportedType(Class<?> c) {
        return true;
    }
}
