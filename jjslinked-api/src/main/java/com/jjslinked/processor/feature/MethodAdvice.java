package com.jjslinked.processor.feature;

import java.lang.annotation.Annotation;

public interface MethodAdvice {

    Class<? extends Annotation> getAnnotation();
}
