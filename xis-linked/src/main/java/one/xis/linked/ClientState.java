package one.xis.linked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface ClientState {
    String value() default "";

    String key() default "";

    ClientScope scope() default ClientScope.INVOKER;
}
