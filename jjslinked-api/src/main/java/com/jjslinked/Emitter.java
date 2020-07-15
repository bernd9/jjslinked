package com.jjslinked;

import com.ejc.AdviceClass;
import com.jjslinked.processor.emitter.EmitterAdvice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@AdviceClass(EmitterAdvice.class)
public @interface Emitter {
}
