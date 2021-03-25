package one.xis.sql.processor;

import com.squareup.javapoet.TypeName;
import lombok.Getter;
import one.xis.sql.Column;
import one.xis.sql.NamingRules;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Optional;

abstract class FieldModel {
    @Getter
    protected final VariableElement field;
    protected final ExecutableElement getter;
    protected final ExecutableElement setter;

    @Getter
    protected final EntityModel entityModel;

    FieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        this.entityModel = entityModel;
        this.field = field;
        this.getter = gettersAndSetters.getGetter(field).orElse(null);
        this.setter = gettersAndSetters.getSetter(field).orElse(null);
    }

    String getColumnName() {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return NamingRules.toSqlName(getFieldName().toString());
    }
    
    Optional<ExecutableElement> getGetter() {
        return Optional.ofNullable(getter);
    }

    // TODO check: setter must be friendly
    Optional<ExecutableElement> getSetter() {
        return Optional.ofNullable(setter);
    }


    Name getFieldName() {
        return field.getSimpleName();
    }

    TypeMirror getFieldType() {
        return field.asType();
    }

    <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return field.getAnnotation(annotationType);
    }

    @Override
    public boolean equals(Object obj) {
       return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return getEntityModel().getTypeName() + "." + getField().getSimpleName();
    }
}
