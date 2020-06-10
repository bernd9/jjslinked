package com.jjslinked;

import com.ejaf.ParameterContext;
import com.ejaf.ParameterProvider;

class MessageParameterProvider implements ParameterProvider<IncomingMessageEvent> {

    @Override
    public <R> R getParameter(ParameterContext parameterContext, IncomingMessageEvent event, Class<R> parameterType) {
        return null;
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return true;
    }
}
