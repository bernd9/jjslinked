package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForeignKeyToNullAction<E, C> {
    private final Class<E> entityClass;
    private final String columnName;
    private final Class<C> columnValueType;

    void doSetToNull(@NonNull C currentColumnValue) {

    }
}
