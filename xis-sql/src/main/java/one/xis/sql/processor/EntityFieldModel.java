package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;
import one.xis.sql.*;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Objects;
import java.util.Optional;

@Getter
class EntityFieldModel {
    private final VariableElement field;
    private final Optional<ExecutableElement> getter;
    private final Optional<ExecutableElement> setter;
    private Types types;

    EntityFieldModel(VariableElement field, Optional<ExecutableElement> getter, Optional<ExecutableElement> setter) {
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        //this.types = types;
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

    boolean isForeignKey() { // TODO validate ForeignKey can not be used to annotate collections, arrays etc
        return field.getAnnotation(ForeignKey.class) != null;
    }

    Optional<String> getForeignKeyColumnName() {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        if (foreignKey == null) {
            return Optional.empty();
        }
        EntityModel entityModel = EntityModel.getEntityModel(getFieldType());
        if (entityModel == null) {
            return Optional.empty();
        }
        return Optional.of(entityModel.getTableName() + "_id");
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
        EntityModel fieldEntityModel = getFieldEntityModel();
        return crossTable.columnName().isEmpty() ? NamingRules.toForeignKeyName(fieldEntityModel.getType().asType()) : crossTable.columnName();
    }

    EntityModel getFieldEntityModel() {
        if (!isEntityField()) throw new IllegalStateException("not an entity field");
        TypeMirror entityType;
        if (isCollection()) {
            entityType = JavaModelUtils.getGenericCollectionType(field);
        } else {
            entityType = field.asType();
        }
        return EntityModel.getEntityModel(entityType);
    }

    String getColumnName() {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return NamingRules.toSqlName(getFieldName().toString());
    }

    boolean isInsertBeforeEntityField() {
        return isEntityField() && isForeignKey();
    }

}
