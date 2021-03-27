package com.ejc;

import java.lang.annotation.*;

/**
 * Annotated element has to be an instance of {@link MethodAdvice}.
 * Using the annotation will initiate creating an subclass or implementation
 * of this method's declaring class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Advice {
    Class<? extends Annotation> annotation();

    AdviceTarget[] targets();

    int priority() default 0;

}
