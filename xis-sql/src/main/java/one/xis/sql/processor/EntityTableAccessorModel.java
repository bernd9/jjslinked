package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityTableAccessorModel {

    @Getter
    private final EntityModel entityModel;

    String getEntityTableAccessorSimpleName() {
        return getEntityTableAccessorSimpleName(entityModel);
    }

    String getEntityTableAccessorPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityTableAccessorSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "TableAccessor";
    }
    
    String getEntityProxySimpleName() {
        return EntityProxyModel.getEntityProxySimpleName(entityModel);
    }
}
