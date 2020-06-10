package com.ejaf;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ParameterContext {
    private final Annotations annotations;
    private final String paramName;
    private final MethodInvocationContext methodInvocationContext;
}
