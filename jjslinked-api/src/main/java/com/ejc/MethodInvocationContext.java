package com.ejc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public class MethodInvocationContext {
    private final Annotations annotations;
    private final Method method;

}
