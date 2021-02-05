package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DeleteAction<E, C> {
    private final Class<E> entityClass;
    private final String deleteByColumnName;
    private final C deleteByColumnValue;

    void doDelete() {

    }
}
