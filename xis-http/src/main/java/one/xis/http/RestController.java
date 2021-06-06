package one.xis.http;

import one.xis.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Singleton
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RestController {
    String value() default "";
}
