package one.xis.sql.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
class EntityUtilModel {

    @Getter
    @Delegate
    private final EntityModel entityModel;

    String getEntityUtilSimpleName() {
        return getEntityUtilSimpleName(entityModel);
    }

    String getEntityUtilPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityUtilSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "Util";
    }

    static TypeName getEntityUtilTypeName(EntityModel entityModel) {
        return ClassName.get(entityModel.getPackageName(), getEntityUtilSimpleName(entityModel));
    }

    TypeName getEntityUtilTypeName() {
        return getEntityUtilTypeName(entityModel);
    }

}
