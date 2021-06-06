package com.ejc.http.api.controller;

import one.xis.context.ClassReference;
import com.ejc.util.TypeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForQueryParameter implements ParameterProvider<Object> {
    private final String parameterName;
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        String param = context.getRequest().getParameter(parameterName);
        return TypeUtils.convertStringToSimple(param, parameterType.getReferencedClass());
    }
}
