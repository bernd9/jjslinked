package com.ejc.sql.processor;

import javax.lang.model.element.VariableElement;

class ExternalForeignKeyAttribute extends EntityField {
    public ExternalForeignKeyAttribute(VariableElement field, String tableName, String columnName) {
        super(field, tableName, columnName);
    }
}
