package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import one.xis.sql.Column;
import one.xis.sql.ForeignKey;
import one.xis.sql.NamingRules;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

// TODO validate ForeignKey can not be used to annotate collections, arrays etc
public class ForeignKeyFieldModel extends EntityFieldModel {
    public ForeignKeyFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
    }

    @Override
    String getColumnName() {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        if (!foreignKey.columnName().isEmpty()) {
            return foreignKey.columnName();
        }
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        EntityModel fieldEntityModel = EntityModel.getEntityModel(getFieldType());
        return fieldEntityModel.getTableName() + "_" + NamingRules.toSqlName(fieldEntityModel.getIdField().getFieldName().toString());
    }

}
