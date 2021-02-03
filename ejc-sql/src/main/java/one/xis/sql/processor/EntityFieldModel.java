package one.xis.sql.processor;

import com.ejc.sql.*;
import com.ejc.sql.api.ORNameMapper;
import com.ejc.util.JavaModelUtils;
import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Optional;

@Getter
class EntityFieldModel {
    private final EntityModel entityModel;
    private final VariableElement field;
    private Types types;
    private Optional<ExecutableElement> getter;
    private Optional<ExecutableElement> setter;

    EntityFieldModel(EntityModel entityModel, VariableElement field, Types types) {
        this.entityModel = entityModel;
        this.field = field;
        this.types = types;
        GettersAndSetters gettersAndSetters = new GettersAndSetters(entityModel.getType(), types);
        this.getter = gettersAndSetters.getGetter(field);
        this.setter = gettersAndSetters.getSetter(field);
    }

    Name getFieldName() {
        return field.getSimpleName();
    }

    TypeMirror getFieldType() {
        return field.asType();
    }

    boolean isNonComplex() {
        return JavaModelUtils.isNonComplex(field.asType());
    }

    boolean isEntity() {
        return field.asType().getAnnotation(Entity.class) != null;
    }

    boolean isForeignEntityTable() {
        ForeignKeyRef foreignKeyRef = field.getAnnotation(ForeignKeyRef.class);
        if (foreignKeyRef != null) {
            return !foreignKeyRef.table().equals(entityModel.getTableName());
        }
        return false;
    }

    boolean isForeignCrossTable() {
        return field.getAnnotation(CrossTableRef.class) != null;
    }

    boolean isForeignTable() {
        return isForeignCrossTable() || isForeignEntityTable();
    }

    boolean isId() {
        return field.getAnnotation(Id.class) != null;
    }

    Optional<String> getForeignEntityTable() {
        ForeignKeyRef foreignKeyRef = field.getAnnotation(ForeignKeyRef.class);
        if (foreignKeyRef != null && !foreignKeyRef.table().equals(entityModel.getTableName())) {
            return Optional.of(foreignKeyRef.table());
        }
        return Optional.empty();
    }

    Optional<String> getForeignCrossTable() {
        return Optional.ofNullable(field.getAnnotation(CrossTableRef.class)).map(CrossTableRef::table);
    }

    String getColumnName() {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return ORNameMapper.toSqlName(getFieldName().toString());
    }

    boolean isInsertBeforeField() {
        if (isForeignEntityTable()) {
            ForeignKeyRef foreignKeyRef = field.getAnnotation(ForeignKeyRef.class);
            return foreignKeyRef.table().equals(entityModel.getTableName());// We need the pk of that entity
        }
        return true;
    }
    
}
