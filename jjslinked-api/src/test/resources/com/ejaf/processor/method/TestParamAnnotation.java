package com.ejaf.processor;

import com.ejaf.ParameterProviderAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@ParameterProviderAnnotation(TestProvider.class)
public @interface TestParamAnnotation {
}
