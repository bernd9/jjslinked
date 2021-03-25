package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
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
        return StringUtils.firstToLowerCase(getInnerClassName());
    }

    TypeElement getEntityType() {
        return entityFieldModel.getEntityModel().getType();
    }

    TypeMirror getEntityIdType() {
        return entityFieldModel.getEntityModel().getIdField().getFieldType();
    }

    TypeMirror getCollectionsGenericType() {
        return JavaModelUtils.getGenericCollectionType(entityFieldModel.getField());
    }

    TypeMirror getFieldIdType() {
        return entityFieldModel.getEntityModel().getIdField().getFieldType();
    }
}
