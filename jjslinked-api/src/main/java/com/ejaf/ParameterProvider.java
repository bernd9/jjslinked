package com.ejaf;

public interface ParameterProvider<E extends InvokerEvent> {

    <T> T getParameter(ParameterContext parameterContext, E event, Class<T> type);

    boolean isSupportedParameterType(Class<?> c);
}
