package com.jjslinked;

import com.ejaf.ParameterContext;
import com.jjslinked.model.ClientMessage;

class MessageParameterProvider implements ParameterProvider {

    @Override
    public <R> R getParameter(ParameterContext parameterContext, ClientMessage message) {
        return null;
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return true;
    }
}
