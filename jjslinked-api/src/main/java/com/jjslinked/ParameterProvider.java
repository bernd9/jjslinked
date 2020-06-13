package com.jjslinked;

import com.ejaf.ParameterContext;
import com.jjslinked.model.ClientMessage;

public interface ParameterProvider {

    <R> R getParameter(ParameterContext parameterContext, ClientMessage message);

    boolean isSupportedParameterType(Class<?> c);
}
