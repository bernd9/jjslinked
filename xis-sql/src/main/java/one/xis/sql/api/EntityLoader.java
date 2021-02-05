package one.xis.sql.api;

import java.util.Collection;

public interface EntityLoader<E> {
    E findByColumnValue(String columnName, Object value);

    <C extends Collection<E>> C findAllByColumnValue(String columnName, Object value, Class<C> collectionType);
}
