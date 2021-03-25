package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import lombok.Getter;
import one.xis.sql.Referenced;

import javax.lang.model.element.VariableElement;

@Getter
public class ReferencedFieldModel extends EntityFieldModel {
    private final ForeignKeyFieldModel referringField;

    public ReferencedFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters, ForeignKeyFieldModel referringField) {
        super(entityModel, field, gettersAndSetters);
        this.referringField = referringField;
    }

    boolean isDeleteUnlinked() {
        Referenced referenced = getAnnotation(Referenced.class);
        if (referenced == null) {
            return false;
        }
        return referenced.deleteUnlinked();
    }


    String getFieldHandlerName() {
        return StringUtils.firstToUpperCase(getFieldName().toString()) + "FieldHandler";
    }

}
