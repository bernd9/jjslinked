package com.jjslinked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Registry {
    String name();

    String key();

    String superClass() default "com.jjslinked.SimpleRegistry"; // TODO Speciel fpr providers
}
