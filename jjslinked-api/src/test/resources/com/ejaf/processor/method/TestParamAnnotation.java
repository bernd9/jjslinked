package com.ejaf.processor;

import com.ejaf.Provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@Provider(TestProvider.class)
public @interface TestParamAnnotation {
}
