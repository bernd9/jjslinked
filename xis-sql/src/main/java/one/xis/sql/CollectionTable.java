package one.xis.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used if a collection of non-complex values has to be stored in
 * a separate table with a foreign to the enclosing entity.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface CollectionTable {
    String tableName();

    String foreignColumnName();
}
