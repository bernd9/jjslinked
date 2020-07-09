package com.ejc.processor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValueInjectorBase {
    private final String declaringClass;
    private final String fieldName;
    private final String propertyName;
}
