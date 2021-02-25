package one.xis.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static one.xis.sql.GenerationStrategy.DBMS;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Id {
    GenerationStrategy generationStrategy() default DBMS;
}
