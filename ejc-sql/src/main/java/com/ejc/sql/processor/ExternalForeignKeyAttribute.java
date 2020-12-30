package com.ejc.sql.processor;

import javax.lang.model.element.TypeElement;

class ExternalForeignKeyAttribute extends EntityField {
    public ExternalForeignKeyAttribute(String fieldName, TypeElement fieldType, String columnName, int sqlType) {
        super(fieldName, fieldType, columnName, sqlType);
    }
}
