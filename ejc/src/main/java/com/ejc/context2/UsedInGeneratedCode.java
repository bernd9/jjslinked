package com.ejc.context2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface UsedInGeneratedCode {
    Class<?> value();
}
