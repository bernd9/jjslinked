package com.ejc.http.api.controller;

import com.ejc.util.TypeUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParameterProviderForUrlParam<T> implements ParameterProvider<T> {
    private final String parameterKey;
    private final Class<T> parameterType;

    @Override
    public T provide(ControllerMethodInvocationContext context) {
        String param = context.getPathVariables().get(parameterKey);
        return TypeUtils.convertSimple(param, parameterType);
    }
}
