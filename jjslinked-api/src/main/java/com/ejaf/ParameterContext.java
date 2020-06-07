package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@Getter
@RequiredArgsConstructor
public class ParameterContext<A extends Annotation> {
    private final A annotation;
    private final String paramName;
}
