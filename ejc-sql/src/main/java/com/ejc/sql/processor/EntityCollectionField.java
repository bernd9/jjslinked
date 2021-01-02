package com.ejc.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class EntityCollectionField extends EntityField {

    @Getter
    private final TypeMirror elementType;

    public EntityCollectionField(VariableElement variableElement, String tableName, String columnName, Optional<ExecutableElement> getter, Optional<ExecutableElement> setter) {
        super(variableElement.getSimpleName(), variableElement.asType(), tableName, columnName, getter, setter);
        this.elementType = JavaModelUtils.getGenericCollectionType(variableElement);
    }
}
