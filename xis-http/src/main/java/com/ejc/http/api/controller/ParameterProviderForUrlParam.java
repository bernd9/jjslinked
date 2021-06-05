package com.ejc.http.api.controller;

import com.ejc.api.context.ClassReference;
import com.ejc.util.TypeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForUrlParam implements ParameterProvider<Object> {
    private final String parameterKey;
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        String param = context.getPathVariables().get(parameterKey);
        return TypeUtils.convertStringToSimple(param, parameterType.getReferencedClass());
    }
}
