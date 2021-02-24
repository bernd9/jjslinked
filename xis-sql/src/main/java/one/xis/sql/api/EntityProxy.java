package one.xis.sql.api;

public interface EntityProxy<E, ID> {
    ID getPk();

    E getEntity();

    <T> T getForeignKeyValue(String fieldName);

    boolean isDirty();
}
