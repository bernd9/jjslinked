package com.ejc.sql.processor;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

@Getter
@Builder
@RequiredArgsConstructor
class EntityField {
    private final Name fieldName;
    private final TypeMirror fieldType;
    private final String tableName;
    private final String columnName;
    private final Optional<ExecutableElement> getter;
    private final Optional<ExecutableElement> setter;

    EntityField(VariableElement e, String tableName, String columnName, Optional<ExecutableElement> getter, Optional<ExecutableElement> setter) {
        this.fieldName = e.getSimpleName();
        this.fieldType = e.asType();
        this.tableName = tableName;
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
    }

}
