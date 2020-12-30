package com.ejc.sql.processor;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

@Getter
class CollectionAttribute extends EntityField {
    private final Class<? extends Collection<?>> collectionType;

    @Builder
    public CollectionAttribute(String fieldName, TypeElement fieldType, String columnName, int sqlType, Class<? extends Collection<?>> collectionType) {
        super(fieldName, fieldType, columnName, sqlType);
        this.collectionType = collectionType;
    }
}
