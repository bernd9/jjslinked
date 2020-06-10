package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MethodParameter {
    private final Annotations annotations;
    private final String paramName;
    private final Object value;
    private final String valueType;
}
