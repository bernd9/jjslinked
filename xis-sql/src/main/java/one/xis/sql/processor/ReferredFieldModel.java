package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import one.xis.sql.Referred;

import javax.lang.model.element.VariableElement;
import java.util.Objects;

public class ReferredFieldModel extends EntityFieldModel {

    private Referred referred;

    public ReferredFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
        this.referred = field.getAnnotation(Referred.class);
        Objects.requireNonNull(this.referred);
    }

    ForeignKeyFieldModel getExternalForeignKeyField() {
        String fieldNameOrColumn = referred.value();
        EntityModel referringModel = getFieldEntityModel();
        return referringModel.getForeignKeyFields().stream()
                .filter(field -> foreignKeyFieldMatches(field, fieldNameOrColumn))
                .collect(CollectorUtils.toOnlyElement());
    }

    private boolean foreignKeyFieldMatches(ForeignKeyFieldModel foreignKeyField, String name) {
        return foreignKeyField.getForeignKeyColumnName().equals(name) || foreignKeyField.getFieldName().toString().equals(name);
    }
}
