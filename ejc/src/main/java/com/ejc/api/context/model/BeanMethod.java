package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class BeanMethod {
    private final String name;
    private final ClassReference returnType;
}
