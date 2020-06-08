package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MethodInvocationContext {
    private final Set<? extends Annotation> annotations;
    private final Method method;

}
