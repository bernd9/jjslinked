package com.ejc;

import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Set;

@RequiredArgsConstructor
public class Annotations {
    private final Set<? extends Annotation> annotations;

    public boolean contains(Class<? extends Annotation> annotationClass) {
        return annotations.stream().anyMatch(annotationClass::isInstance);
    }

    public <A extends Annotation> A get(Class<A> annotationClass) {
        return (A) annotations.stream().filter(annotationClass::isInstance).findFirst().orElseThrow();
    }
}
