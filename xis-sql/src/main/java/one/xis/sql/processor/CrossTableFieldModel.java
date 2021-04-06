package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import com.ejc.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import one.xis.sql.CrossTable;

import javax.lang.model.element.VariableElement;

class CrossTableFieldModel extends EntityFieldModel {

    private final CrossTable crossTable;

    @Getter
    @Setter
    private CrossTableFieldModel correspondingCrossTableField;

    @Builder
    public CrossTableFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
        crossTable = field.getAnnotation(CrossTable.class);

        // TODO : code fails here. move to validator
        //if (!JavaModelUtils.isCollection(field)) {
         //   throw new IllegalStateException(field + " must be a collection. It is annotated with @CrossTable");
        //}
    }
    String getCrossTable() {
        return crossTable.tableName();
    }

    String getEntityColumnNameInCrossTable() {
        if (!crossTable.columnName().isEmpty()) {
            return crossTable.columnName();
        }
        return NamingRules.toSqlName(entityModel.getSimpleName() + "_id");
    }


    String getFieldColumnNameInCrossTable() {
        return correspondingCrossTableField.getColumnName();
    }

    @Override
    boolean isCollection() {
        return true;
    }


    String getFieldHandlerName() {
        return StringUtils.firstToUpperCase(getFieldName().toString()) + "FieldHandler";
    }

}
