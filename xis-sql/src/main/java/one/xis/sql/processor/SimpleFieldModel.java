package one.xis.sql.processor;

import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class SimpleFieldModel {
    @Getter
    protected final EntityModel entityModel;

    @Getter
    protected final VariableElement field;
    protected final ExecutableElement getter;
    protected final ExecutableElement setter;

    SimpleFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        this.entityModel = entityModel;
        this.field = field;
        this.getter = gettersAndSetters.getGetter(field).orElse(null);
        this.setter = gettersAndSetters.getSetter(field).orElse(null);
    }

    Optional<ExecutableElement> getGetter() {
        return Optional.ofNullable(getter);
    }

    Optional<ExecutableElement> getSetter() {
        return Optional.ofNullable(setter);
    }


    Name getFieldName() {
        return field.getSimpleName();
    }

    TypeMirror getFieldType() {
        return field.asType();
    }

}
