package one.xis.sql.processor;

import one.xis.sql.ForeignKey;
import one.xis.sql.NamingRules;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

// TODO validate ForeignKey can not be used to annotate collections, arrays etc
public class ForeignKeyFieldModel extends EntityFieldModel {
    public ForeignKeyFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
    }
    
    Optional<String> getForeignKeyColumnName() {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        if (foreignKey == null) {
            return Optional.empty();
        }
        EntityModel fieldEntityModel = EntityModel.getEntityModel(getFieldType());
        if (fieldEntityModel == null) {
            return Optional.empty();
        }
        return Optional.of(fieldEntityModel.getTableName() + "_" + NamingRules.toSqlName(fieldEntityModel.getIdField().getFieldName().toString()));
    }
}
