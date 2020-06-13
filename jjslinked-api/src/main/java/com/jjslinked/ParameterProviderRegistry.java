package com.jjslinked;

import java.lang.annotation.Annotation;

public interface ParameterProviderRegistry {

    ParameterProvider getProvider(Class<? extends Annotation>... annotationClasses);
}
