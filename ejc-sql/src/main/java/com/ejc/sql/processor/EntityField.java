package com.ejc.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@Getter
@RequiredArgsConstructor
class EntityField {
    private final Name fieldName;
    private final TypeMirror fieldType;
    private final String tableName;
    private final String columnName;

    EntityField(VariableElement e, String tableName, String columnName) {
        this.fieldName = e.getSimpleName();
        this.fieldType = e.asType();
        this.tableName = tableName;
        this.columnName = columnName;

    }

}
