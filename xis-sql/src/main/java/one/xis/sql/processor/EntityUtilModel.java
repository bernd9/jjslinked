package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
class EntityUtilModel {

    @Getter
    @Delegate
    private final EntityModel entityModel;

    String getEntityUtilSimpleClassName() {
        return getEntityUtilSimpleClassName(entityModel);
    }

    String getEntityUtilPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityUtilSimpleClassName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "Util";
    }

    String getCopyAttributesMethodName() {
        return "copyAttributes";
    }
}
