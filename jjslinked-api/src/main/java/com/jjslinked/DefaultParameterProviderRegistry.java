package com.jjslinked;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class DefaultParameterProviderRegistry implements ParameterProviderRegistry {


    @Override
    public Class<? extends ParameterProvider> getProviderClass(Collection<? extends Annotation> annotationClasses) {
        return null;
    }
}
