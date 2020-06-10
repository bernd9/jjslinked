package com.jjslinked;

import com.ejaf.ParameterContext;
import com.ejaf.ParameterProvider;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.Set;

class UserIdParameterProvider implements ParameterProvider<IncomingMessageEvent> {

    private Set<Class<?>> PARAMETER_TYPES = Set.of(String.class, Integer.class, Long.class, Integer.TYPE, Long.TYPE);

    @Override
    public <R> R getParameter(ParameterContext parameterContext, IncomingMessageEvent event, Class<R> parameterType) {
        return event.getClientMessage().getUserId().map(userId -> (R) ConvertUtils.convert(userId, parameterType)).orElseThrow(SecurityException::new);
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return PARAMETER_TYPES.contains(c);
    }
}
