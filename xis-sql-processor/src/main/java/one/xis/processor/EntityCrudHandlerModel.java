package one.xis.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class EntityCrudHandlerModel {
    private final EntityModel entityModel;

    String getForeignKeyFieldHandlerInnerClassName(ForeignKeyFieldModel model) {
        return NamingRules.toJavaClassName(model.getColumnName()) + "ForeignKeyFieldHandler";
    }


    String getReferencedFieldHandlerInnerClassName(ReferencedFieldModel model) {
        return NamingRules.toJavaClassName(model.getColumnName()) + "ReferencedFieldHandler";
    }

    static ClassName getCrudHandlerTypeName(EntityModel entityModel) {
        return ClassName.get(getCrudHandlerPackageName(entityModel), getCrudHandlerSimpleName(entityModel));
    }

    static String getCrudHandlerSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "CrudHandler";
    }

    static String getCrudHandlerPackageName(EntityModel entityModel) {
        return entityModel.getPackageName();
    }


    String getCrudHandlerSimpleName() {
        return getCrudHandlerSimpleName(entityModel);
    }

    String getCrudHandlerPackageName() {
       return getCrudHandlerPackageName(entityModel);
    }

    public TypeName getEntityProxyTypeName() {
        return EntityProxyModel.getEntityProxyTypeName(entityModel);
    }

}
