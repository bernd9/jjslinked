package one.xis.sql.processor;

import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.NamingRules;

@Getter
@RequiredArgsConstructor
class EntityCrudHandlerModel {
    private final EntityModel entityModel;

    String getForeignKeyHandlerInnerClassName(ForeignKeyFieldModel model) {
        return NamingRules.toJavaClassName(model.getColumnName()) + "ForeignKeyFieldHandler";
    }

    public String getCrudHandlerSimpleName() {
        return entityModel.getSimpleName() + "CrudHandler";
    }

    public String getCrudHandlerPackageName() {
        return entityModel.getPackageName();
    }

    public TypeName getEntityProxyTypeName() {
        return EntityProxyModel.getEntityProxyTypeName(entityModel);
    }
}
