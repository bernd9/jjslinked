package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import com.squareup.javapoet.ClassName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.type.TypeMirror;

@Getter
@RequiredArgsConstructor
class CrossTableAccessorModel {
    private final CrossTableFieldModel crossTableFieldModel;

    String getCrossTable() {
        return crossTableFieldModel.getCrossTable();
    }

    String getEntityColumnNameInCrossTable() {
        return crossTableFieldModel.getEntityColumnNameInCrossTable();
    }

    String getFieldColumnNameInCrossTable() {
        return crossTableFieldModel.getCorrespondingCrossTableField().getColumnName();
    }

    TypeMirror getEntityKeyType() {
        return crossTableFieldModel.getEntityModel().getIdField().getFieldType();
    }

    TypeMirror getFieldKeyType() {
        return crossTableFieldModel.getFieldEntityModel().getIdField().getFieldType();
    }

    String getCrossTableAccessorPackageName() {
        return getCrossTableAccessorPackageName(crossTableFieldModel.getEntityModel());
    }

    String getCrossTableAccessorSimpleName() {
        return getCrossTableAccessorSimpleName(crossTableFieldModel.getEntityModel(), crossTableFieldModel.getCorrespondingCrossTableField().getEntityModel());
    }


    static String getCrossTableAccessorPackageName(EntityModel entityModel) {
        return entityModel.getPackageName();
    }


    static String getCrossTableAccessorSimpleName(EntityModel entityModel, EntityModel fieldEntityModel) {
        return new StringBuilder()
                .append(entityModel.getSimpleName())
                .append(fieldEntityModel.getSimpleName())
                .append("CrossTableAccessor")
                .toString();
    }

    static ClassName getCrossTableAccessorTypeName(EntityModel entityModel, EntityModel fieldEntityModel) {
        return ClassName.get(getCrossTableAccessorPackageName(entityModel), getCrossTableAccessorSimpleName(entityModel, fieldEntityModel));
    }

    static ClassName getCrossTableAccessorTypeName(CrossTableFieldModel crossTableFieldModel) {
        return getCrossTableAccessorTypeName(crossTableFieldModel.getEntityModel(), crossTableFieldModel.getFieldEntityModel());
    }
}
