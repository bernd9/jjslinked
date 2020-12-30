package com.ejc.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

class EntityCollectionField extends EntityField {

    @Getter
    private final TypeMirror elementType;

    public EntityCollectionField(VariableElement variableElement, String tableName, String columnName) {
        super(variableElement.getSimpleName(), variableElement.asType(), tableName, columnName);
        this.elementType = JavaModelUtils.getGenericCollectionType(variableElement);
    }
}
