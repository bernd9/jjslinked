package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DeleteAction<E, C> {
    private final Class<E> entityClass;
    private final String deleteByColumnName;

    void doDelete(@NonNull C deleteByColumnValue) {

    }
}
