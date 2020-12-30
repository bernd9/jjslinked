package com.ejc.sql.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityFields<E, ID> {
    private final String entityName;
    private final String tableName;
    private ID id;

    @Getter
    private boolean updated;
    private E entity;

    void setId(ID id) {
        this.id = id;
    }

    void fieldValueChanged() {
        updated = true;
    }


    void foreignKeyChanged(String referringTable, String referringColumn, Class<?> entityClass) {
        if (referringTable.equals(tableName)) {

        } else {
            
        }
    }


    <E extends Iterable<I>, I> E getIterable(Class<?> owner, Object id, Class<E> entityClass, Class<I> iterableClass) {
        return null;
    }

    ID getId() {
        return id;
    }
}
