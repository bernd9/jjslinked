package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ColumnUpdateAction<E, C> {
    private final Class<E> entityClass;
    private final String columnName;
    private final Class<C> columnValueType;
    private final C oldColumnValue;
    private final C newColumnValue;

    void doUpdate() {

    }
}
