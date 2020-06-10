package com.jjslinked;

import com.ejaf.Mapped;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Mapped(deciderClass = ClientCallDecider.class, defaultParameterProvider = MessageParameterProvider.class)
public @interface ClientCall {
}
