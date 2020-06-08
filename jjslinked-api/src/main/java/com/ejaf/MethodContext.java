package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@Getter
@RequiredArgsConstructor
public class MethodContext<A extends Annotation, C extends MethodInvocationContext> {
    private final A annotation;
    private final C invocationContext;
    private final ApplicationContext applicationContext;
}
