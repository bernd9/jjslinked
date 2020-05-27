package com.jjslinked.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface LinkedObservable {
    String fieldName() default "";
}
