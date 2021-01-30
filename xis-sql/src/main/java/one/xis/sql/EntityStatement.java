package one.xis.sql;

public interface EntityStatement<E> {
    int execute(E entity);
}
