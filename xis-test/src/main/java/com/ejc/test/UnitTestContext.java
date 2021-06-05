package com.ejc.test;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@Getter
@Builder
public class UnitTestContext {
    private final Object test;
    private final Map<Class<? extends Annotation>, Collection<Field>> testAnnotatedFields;
    //private final Map<? extends Annotation, Collection<Method>> testAnnotatedMethods;
}
