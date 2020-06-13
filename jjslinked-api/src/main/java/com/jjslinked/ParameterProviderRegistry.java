package com.jjslinked;

import java.lang.annotation.Annotation;

public interface ParameterProviderRegistry {

    Class<? extends ParameterProvider> getProviderClass(Class<? extends Annotation>... annotationClasses);
}
