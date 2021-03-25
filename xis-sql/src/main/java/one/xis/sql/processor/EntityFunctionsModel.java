package one.xis.sql.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

class EntityFunctionsModel extends EntityUtilModel{

    public EntityFunctionsModel(EntityModel entityModel) {
        super(entityModel);
    }

    String getEntityFunctionsPackageName() {
        return getEntityFunctionsPackageName(getEntityModel());
    }

    String getEntityFunctionsSimpleName() {
        return getEntityFunctionsSimpleName(getEntityModel());
    }

    ClassName getEntityFunctionsTypeName() {
        return getEntityFunctionsTypeName(getEntityModel());
    }

    TypeName getPkTypeName() {
        return  TypeName.get(getIdField().getFieldType());
    }

    static ClassName getEntityFunctionsTypeName(EntityModel entityModel) {
        return ClassName.get(getEntityFunctionsPackageName(entityModel), getEntityFunctionsSimpleName(entityModel));
    }

    static String getEntityFunctionsPackageName(EntityModel entityModel) {
        return entityModel.getPackageName();
    }

    static String getEntityFunctionsSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "Functions";
    }
}
