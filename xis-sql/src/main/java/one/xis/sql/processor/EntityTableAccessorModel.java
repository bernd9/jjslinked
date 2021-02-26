package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityTableAccessorModel {

    @Getter
    private final EntityModel entityModel;

    String getEntityTableAccessorSimpleName() {
        return entityModel.getSimpleName() + "TableAccessor";
    }

    String getEntityTableAccessorPackageName() {
        return entityModel.getPackageName();
    }


}
