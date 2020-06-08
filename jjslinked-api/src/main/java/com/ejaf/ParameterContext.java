package com.ejaf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ParameterContext {
    private final Set<? extends Annotation> annotations;
    private final String paramName;
    private final MethodInvocationContext methodInvocationContext;
    private final ApplicationContext applicationContext;

    <A extends Annotation> Optional<A> getAnnotation(Class<A> type) {
        return annotations.stream().filter(a -> type.isInstance(a)).map(type::cast).findFirst();
    }
}
