package one.xis.sql.api;

public interface EntityProxy<E, EID> {
    EID pk();

    E getEntity();

    boolean isDirty();

    void setClean();

    void setPkPrivileged(EID id);
}
