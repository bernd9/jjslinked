package com.jjslinked;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultParameterProviderRegistry implements ParameterProviderRegistry {

    @Override
    public Class<? extends ParameterProvider> getProviderClass(Class<? extends Annotation>... annotationClasses) {
        Set<Class<? extends Annotation>> annotations = new HashSet<>(Arrays.asList(annotationClasses)); // Java 8 compatible
        if (annotations.contains(UserId.class)) {
            return UserIdParameterProvider.class;
        }
        if (annotations.contains(ClientId.class)) {
            return ClientIdParameterProvider.class;
        }
        return null;
    }
}
