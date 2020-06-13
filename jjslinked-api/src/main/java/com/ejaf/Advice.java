package com.ejaf;


import com.jjslinked.ParameterProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Advice {
    Class<? extends MethodAdvice> methodAdvice();

    Class<? extends ParameterProvider> defaultProvider();
}
