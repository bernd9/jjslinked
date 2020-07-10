package com.jjslinked.parameter;

import com.jjslinked.ClientMessage;

class ClientIdParameterProvider implements ParameterProvider {

    @Override
    public String getParameter(ParameterContext parameterContext, ClientMessage message) {
        return message.getClientId().orElseThrow();
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return c.equals(String.class);
    }
}