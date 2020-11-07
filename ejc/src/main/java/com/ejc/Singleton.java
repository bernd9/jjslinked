package com.ejc;

import com.ejc.context2.UndefinedClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Singleton {
    Class<?> replace() default UndefinedClass.class; // TODO
}
