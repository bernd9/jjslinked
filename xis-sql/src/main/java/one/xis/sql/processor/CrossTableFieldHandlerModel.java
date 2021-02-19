package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@RequiredArgsConstructor
class CrossTableFieldHandlerModel {
    private final EntityFieldModel entityFieldModel;

    String getInnerClassName() {
        return StringUtils.firstToUpperCase(entityFieldModel.getFieldName().toString()) + "FieldHandler";
    }

    String getInstanceName() {
        return StringUtils.firstToLowerCase(getInstanceName());
    }

    TypeElement getEntityType() {
        return entityFieldModel.getEntityModel().getType();
    }

    TypeMirror getEntityIdType() {
        return entityFieldModel.getEntityModel().getIdField().getFieldType();
    }

    TypeMirror getFieldType() {
        return entityFieldModel.getCollectionsGenericType();
    }

    TypeMirror getFieldIdType() {
        return EntityModel.getEntityModel(getFieldType()).getIdField().getFieldType();
    }
}
