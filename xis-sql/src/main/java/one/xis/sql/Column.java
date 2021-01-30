package one.xis.sql;

import javax.lang.model.util.Types;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Column {
   boolean nullable() default true;
   String name() default "";
   String sqlAttributes() default "";
}
