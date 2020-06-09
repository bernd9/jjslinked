package com.ejaf;

public interface ParameterProvider<E extends InvokerEvent> {

    <R> R getParameter(ParameterContext parameterContext, E event, Class<R> parameterType);

    boolean isSupportedParameterType(Class<?> c);
}
