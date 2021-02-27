package one.xis.sql.processor;

import lombok.Getter;
import one.xis.sql.Column;
import one.xis.sql.NamingRules;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Optional;

class FieldModel {
    @Getter
    protected final VariableElement field;
    protected final ExecutableElement getter;
    protected final ExecutableElement setter;

    FieldModel(VariableElement field, GettersAndSetters gettersAndSetters) {
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

}
