package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Builder;
import lombok.NonNull;
import one.xis.sql.CrossTable;
import one.xis.sql.NamingRules;

import javax.lang.model.element.VariableElement;

class CrossTableFieldModel extends EntityFieldModel {

    @NonNull
    private final CrossTable crossTable;

    @Builder
    public CrossTableFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
        crossTable = field.getAnnotation(CrossTable.class);
        if (!JavaModelUtils.isCollection(field)) { // TODO move to validator
            throw new IllegalStateException(field + " must be a collection. It is annotated with @CrossTable");
        }
    }

    String getCrossTable() {
        return crossTable.tableName();
    }

    String getCrossTableColumn() {
        if (!crossTable.columnName().isEmpty()) {
            return crossTable.columnName();
        }
        return NamingRules.toSqlName(getFieldName() + getFieldEntityModel().getIdField().getFieldName().toString());
    }

    @Override
    boolean isCollection() {
        return true;
    }

}
