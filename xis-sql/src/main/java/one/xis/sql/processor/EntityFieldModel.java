package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;
import one.xis.sql.Entity;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@Getter
class EntityFieldModel extends SimpleEntityFieldModel {

    public EntityFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
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

    EntityModel getFieldEntityModel() {
        if (!isEntityField()) {
            throw new IllegalStateException("not an entity field");
        }
        TypeMirror entityType;
        if (isCollection()) {
            entityType = JavaModelUtils.getGenericCollectionType(field);
        } else {
            entityType = field.asType();
        }
        return EntityModel.getEntityModel(entityType);
    }

}
