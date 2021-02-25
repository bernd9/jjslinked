package one.xis.sql.api;

public interface EntityProxy<E, ID> {
    ID pk();

    E getEntity();

    <T> T getForeignKeyValue(String fieldName);

    boolean isDirty();

    void setClean();

    void setPkPrivileged(ID id);
}
