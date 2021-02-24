package one.xis.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static one.xis.sql.Generated.DBMS;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Id {
    Generated generatedBy() default DBMS;
}
