package com.ejc.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UnitTestFieldAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Spy {
}
