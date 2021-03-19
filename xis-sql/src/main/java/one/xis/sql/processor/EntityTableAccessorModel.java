package one.xis.sql.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityTableAccessorModel {

    @Getter
    private final EntityModel entityModel;

    static TypeName getEntityTableAccessorTypeName(EntityModel entityModel) {
        return ClassName.get(entityModel.getPackageName(), getEntityTableAccessorSimpleName(entityModel));
    }

    String getEntityTableAccessorSimpleName() {
        return getEntityTableAccessorSimpleName(entityModel);
    }

    String getEntityTableAccessorPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityTableAccessorSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "TableAccessor";
    }
}
