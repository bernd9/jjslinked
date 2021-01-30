package one.xis.sql;

public @interface Entity {
    String tableName() default "";
}
