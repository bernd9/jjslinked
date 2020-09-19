package com.ejc;

public @interface Value {
    String key();

    String defaultValue() default "";

    boolean mandatory() default true; // TODO
}
