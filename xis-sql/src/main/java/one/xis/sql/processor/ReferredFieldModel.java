package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import com.ejc.util.StringUtils;
import one.xis.sql.ReferringColumn;

import javax.lang.model.element.VariableElement;

public class ReferredFieldModel extends EntityFieldModel {
    private final ForeignKeyFieldModel referringField;

    public ReferredFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters, ForeignKeyFieldModel referringField) {
        super(entityModel, field, gettersAndSetters);
        this.referringField = referringField;
    }

    ForeignKeyFieldModel getExternalForeignKeyField() {
        return referringField;
    }

    private ForeignKeyFieldModel getExternalForeignKeyFieldByAnnotation() {
        String fieldNameOrColumn = getAnnotation(ReferringColumn.class).value();
        EntityModel referringModel = getFieldEntityModel();
        return referringModel.getForeignKeyFields().stream()
                .filter(field -> foreignKeyFieldMatches(field, fieldNameOrColumn))
                .collect(CollectorUtils.toOnlyElement());
    }



    String getFieldHandlerName() {
        return StringUtils.firstToUpperCase(getFieldName().toString())+"FieldHandler";
    }

    private boolean foreignKeyFieldMatches(ForeignKeyFieldModel foreignKeyField, String name) {
        return foreignKeyField.getColumnName().equals(name) || foreignKeyField.getFieldName().toString().equals(name);
    }
}
