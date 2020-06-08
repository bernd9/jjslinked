package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MethodParameter {
    private final Set<? extends Annotation> annotations;
    private final String paramName;
    private final Object value;
    private final String valueType;

    <A extends Annotation> Optional<A> getAnnotation(Class<A> type) {
        return annotations.stream().filter(a -> type.isInstance(a)).map(type::cast).findFirst();
    }
}
