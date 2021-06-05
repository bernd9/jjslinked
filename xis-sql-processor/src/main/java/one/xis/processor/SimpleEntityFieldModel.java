package one.xis.processor;

import javax.lang.model.element.VariableElement;

class SimpleEntityFieldModel extends FieldModel {

    SimpleEntityFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
    }
}
