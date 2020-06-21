package com.jjslinked;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface ParameterProviderRegistry {

    Class<? extends ParameterProvider> getProviderClass(Collection<? extends Annotation> annotationClasses);
}
