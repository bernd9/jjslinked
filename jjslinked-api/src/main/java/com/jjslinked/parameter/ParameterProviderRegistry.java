package com.jjslinked.parameter;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class ParameterProviderRegistry {

    public Class<? extends ParameterProvider> getProviderClass(Collection<? extends Annotation> annotationClasses) {
        if (annotationClasses.contains(UserId.class)) {
            return UserIdParameterProvider.class;
        }
        if (annotationClasses.contains(ClientId.class)) {
            return ClientIdParameterProvider.class;
        }
        return MessageParameterProvider.class;
    }
}
