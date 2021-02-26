package one.xis.sql.processor;

import javax.lang.model.element.VariableElement;

public class JsonFieldModel extends EntityFieldModel {
    JsonFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
    }
}
