package com.ejaf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ListenerMethod {
    Class<? extends InvokerEventDecider<?>> deciderClass();

    Class<? extends ParameterProvider<?>> defaultParameterProvider();
}
