package com.jjslinked;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class MethodContext {
    private final List<? extends Annotation> annotations;
    private final Class<?> declaringClass;
    private final List<Class<?>> parameterTypes;
}
