package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityProxyModel {
    @Getter
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

}
