package com.jjslinked;

public interface ParameterProvider {

    <R> R getParameter(ParameterContext parameterContext, ClientMessage message);

    boolean isSupportedParameterType(Class<?> c);
}
