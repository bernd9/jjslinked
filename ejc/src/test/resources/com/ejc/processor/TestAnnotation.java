package com.ejc.processor;

import com.ejc.HandlerClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HandlerClass(TestMethodHandler.class)
public @interface TestAnnotation {
}
