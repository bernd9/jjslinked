package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import lombok.RequiredArgsConstructor;

import javax.lang.model.type.TypeMirror;

@RequiredArgsConstructor
class CrossTableAccessorModel {
    private final CrossTableFieldModel entityFieldModel;

    String getCrossTableAccessorInnerClassName() {
        return StringUtils.firstToUpperCase(entityFieldModel.getFieldName().toString()) + "CrossTableAccessor";
    }

    String getCrossTableName() {
        return entityFieldModel.getCrossTable();
    }

    String getCrossTableColumnName() {
        return entityFieldModel.getCrossTableColumn();
    }

    TypeMirror getKeyType1() {
        return entityFieldModel.getEntityModel().getIdField().getFieldType();
    }

    TypeMirror getKeyType2() {
        return entityFieldModel.getFieldEntityModel().getIdField().getFieldType();
    }

    String getRemoveReferencesSql() {
        return String.format("DELETE FROM `%s` WHERE `%s` = ?", getCrossTableName(), getCrossTableColumnName());
    }
}
