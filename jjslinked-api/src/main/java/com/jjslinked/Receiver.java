package com.jjslinked;

import com.ejaf.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@ListenerMethod(deciderClass = ReceiverDecider.class, defaultParameterProvider = MessageParameterProvider.class)
public @interface Receiver {
}
