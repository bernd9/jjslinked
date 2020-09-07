package com.ejc.processor;

import com.ejc.api.context.Undefined;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Implementation {
    String forClassName() default "";

    Class<?> forClass() default Undefined.class;
}
