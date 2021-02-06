package one.xis.sql.api;

import java.util.Collection;

abstract class TableAccessor<E> {

    static <E> TableAccessor<E> getInstance(Class<E> entityClass) {
        return null;
    }

    void insertBatch(Collection<E> entities) {

    }

    void updateBatch(Collection<E> entities) {
        
    }
}
