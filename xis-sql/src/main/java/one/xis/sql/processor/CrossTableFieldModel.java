package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.NonNull;
import one.xis.sql.CrossTable;
import one.xis.sql.NamingRules;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Optional;

class CrossTableFieldModel extends EntityFieldModel {

    @NonNull
    private final CrossTable crossTable;

    public CrossTableFieldModel(EntityModel entityModel, VariableElement field, Optional<ExecutableElement> getter, Optional<ExecutableElement> setter) {
        super(entityModel, field, getter, setter);
        crossTable = field.getAnnotation(CrossTable.class);
        if (!JavaModelUtils.isCollection(field)) {
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
