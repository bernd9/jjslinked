package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;
import one.xis.sql.*;
import one.xis.util.Pair;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Objects;

@Getter
class EntityFieldModel {
    private final EntityModel entityModel;
    private final VariableElement field;
    private Types types;

    EntityFieldModel(EntityModel entityModel, VariableElement field, Types types) {
        this.entityModel = entityModel;
        this.field = field;
        this.types = types;
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

    boolean isEntityField() {
        // TODO arrays
        if (!isCollection()) {
            return JavaModelUtils.getGenericCollectionType(field).getAnnotation(Entity.class) != null;
        }
        return field.asType().getAnnotation(Entity.class) != null;
    }

    TypeMirror getCollectionsGenericType() {
        return JavaModelUtils.getGenericCollectionType(field);
    }

    boolean isCollection() {
        return JavaModelUtils.isCollection(field);
    }

    boolean isForeignKey() {
        return field.getAnnotation(ForeignKey.class) != null;
    }

    boolean isUseCrossTable() {
        return field.getAnnotation(CrossTable.class) != null;
    }

    boolean isId() {
        return field.getAnnotation(Id.class) != null;
    }

    String getCrossTable() {
        CrossTable crossTable = field.getAnnotation(CrossTable.class);
        Objects.requireNonNull(crossTable);
        return crossTable.tableName();
    }

    String getCrossTableColumn() {
        CrossTable crossTable = field.getAnnotation(CrossTable.class);
        Objects.requireNonNull(crossTable);
        return crossTable.columnName().isEmpty() ? NamingRules.toForeignKeyName(getEntityModel().getType().asType()) : crossTable.columnName();
    }

    String getColumnName() {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return NamingRules.toSqlName(getFieldName().toString());
    }

    boolean isInsertBeforeField() {
        return isEntityField() && isForeignKey();
    }
    
}
