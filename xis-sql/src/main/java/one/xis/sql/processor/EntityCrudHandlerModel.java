package one.xis.sql.processor;

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
}
