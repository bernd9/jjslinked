package one.xis.processor;

import one.xis.sql.Json;

import javax.lang.model.element.VariableElement;

// TODO validate column-type must match ?
public class JsonFieldModel extends EntityFieldModel {
    JsonFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
    }

    String getCharset() {
        return field.getAnnotation(Json.class).charset();
    }
}
