package com.ejaf;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class MethodContext {
    private final Annotations annotations;
    private final String className;
    private final List<Class<?>> parameterTypes;
    private final String signature;
}
