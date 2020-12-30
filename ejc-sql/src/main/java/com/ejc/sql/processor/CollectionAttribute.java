package com.ejc.sql.processor;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.VariableElement;
import java.util.Collection;

@Getter
class CollectionAttribute extends EntityField {
    private final Class<? extends Collection<?>> collectionType;

    @Builder
    public CollectionAttribute(VariableElement field, String tableName, String columnName, Class<? extends Collection<?>> collectionType) {
        super(field, tableName, columnName);
        this.collectionType = collectionType;
    }
}
