package one.xis.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ResponseAttributes {
    BodyType bodyType() default BodyType.JSON;

    String charset() default "UTF-8";
}
