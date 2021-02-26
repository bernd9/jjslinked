package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityProxyModel {
    @Getter
    private final EntityModel entityModel;


    String getEntityProxySimpleName() {
        return entityModel.getSimpleName() + "Proxy";
    }

    String getEntityProxyPackageName() {
        return entityModel.getPackageName();
    }

}
