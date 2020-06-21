package com.jjslinked;

import lombok.Builder;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;

@Value
@Builder
public class ParameterContext {
    private final Class<?> parameterType;
    private final Collection<? extends Annotation> annotations;
    private final String paramName;
    private final MethodContext methodContext;
}
