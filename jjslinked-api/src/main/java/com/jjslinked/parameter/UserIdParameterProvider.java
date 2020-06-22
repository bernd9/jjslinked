package com.jjslinked.parameter;

import com.jjslinked.ClientMessage;

class UserIdParameterProvider implements ParameterProvider {
    
    @Override
    public String getParameter(ParameterContext parameterContext, ClientMessage message) {
        return message.getUserId().orElseThrow(SecurityException::new);
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return c.equals(String.class);
    }
}
