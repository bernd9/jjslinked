package com.jjslinked;

import org.apache.commons.beanutils.ConvertUtils;

import java.util.Set;

class UserIdParameterProvider implements ParameterProvider {

    private Set<Class<?>> PARAMETER_TYPES = Set.of(String.class, Integer.class, Long.class, Integer.TYPE, Long.TYPE);

    @Override
    public <R> R getParameter(ParameterContext parameterContext, ClientMessage message) {
        return message.getUserId().map(userId -> (R) ConvertUtils.convert(userId, parameterContext.getParameterType())).orElseThrow(SecurityException::new);
    }

    @Override
    public boolean isSupportedParameterType(Class<?> c) {
        return PARAMETER_TYPES.contains(c);
    }
}
