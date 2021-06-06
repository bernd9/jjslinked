package one.xis.processor;

import one.xis.util.JavaModelUtils;
import com.squareup.javapoet.ClassName;
import lombok.Getter;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@Getter
class EntityFieldModel extends FieldModel {

    public EntityFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
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

    ClassName getCrudHandlerName() {
        return EntityCrudHandlerModel.getCrudHandlerTypeName(getFieldEntityModel());
    }
}
