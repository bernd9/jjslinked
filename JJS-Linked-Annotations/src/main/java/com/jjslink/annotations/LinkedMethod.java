package com.jjslink.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface LinkedMethod {
    String clientMethod() default "";
}
