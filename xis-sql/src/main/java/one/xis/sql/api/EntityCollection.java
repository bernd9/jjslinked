package one.xis.sql.api;


interface EntityCollection<E> {
    boolean isDirty();

    void addSilently(E entity);
}
