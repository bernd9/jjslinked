package com.jjslinked;

import com.jjslinked.model.ClientMessage;

public class MessageParameterProvider implements ParameterProvider {

    @Override
    public <R> R getParameter(ParameterContext parameterContext, ClientMessage message) {
        return null;
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return true;
    }
}
