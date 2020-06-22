package com.jjslinked.parameter;

import com.jjslinked.ClientMessage;

public interface ParameterProvider {

    <R> R getParameter(ParameterContext parameterContext, ClientMessage message);

    boolean isSupportedParameterType(Class<?> c);
}
