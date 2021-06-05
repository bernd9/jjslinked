package one.xis.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
class EntityProxyModel {
    @Getter
    @Delegate
    private final EntityModel entityModel;

    String getEntityProxySimpleName() {
        return getEntityProxySimpleName(entityModel);
    }

    String getEntityProxyPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityProxySimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "Proxy";
    }

    static TypeName getEntityProxyTypeName(EntityModel entityModel) {
        return ClassName.get(entityModel.getPackageName(), getEntityProxySimpleName(entityModel));
    }

}
