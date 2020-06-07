package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@Getter
@RequiredArgsConstructor
public class ParameterContext<A extends Annotation, C extends InvocationContext> {
    private final A annotation;
    private final String paramName;
    private final C invocationContext;
    private final ApplicationContext applicationContext;
}
