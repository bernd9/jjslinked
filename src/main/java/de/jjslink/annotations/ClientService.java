package de.jjslink.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ClientService {
    String clientClass() default "";
}
