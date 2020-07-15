package com.ejc;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface AdviceTarget {

    Class<?> declaringClass();

    String signature();
}
