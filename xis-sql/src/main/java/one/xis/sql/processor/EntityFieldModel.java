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

    TypeMirror getCollectionsGenericType() {
        return JavaModelUtils.getGenericCollectionType(field);
    }

    boolean isCollection() {
        return JavaModelUtils.isCollection(field);
    }

    EntityModel getFieldEntityModel() {
        TypeMirror entityType;
        if (isCollection()) {
            entityType = JavaModelUtils.getGenericCollectionType(field);
        } else {
            entityType = field.asType();
        }
        EntityModel fieldEntityModel =EntityModel.getEntityModel(entityType);
        if (fieldEntityModel == null) {
            throw new IllegalStateException("not an entity field");
        }
        return fieldEntityModel;
    }

}
