package com.ejaf.processor;

import com.ejaf.Advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Advice(TestInvoker.class)
public @interface TestInvokerAnnotation {
}
