package one.xis.sql.processor;

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

}
