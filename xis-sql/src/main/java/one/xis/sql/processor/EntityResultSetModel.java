package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;

@Getter
@RequiredArgsConstructor
class EntityResultSetModel {
    private final EntityModel entityModel;
    private final ProcessingEnvironment processingEnvironment;

    String getSimpleName() {
        return entityModel.getSimpleName() + "ResultSet";
    }

    String getPackageName() {
        return entityModel.getPackageName();
    }

    static String getSimpleName(EntityModel entityModel) {
        return EntityProxyModel.getEntityProxySimpleName(entityModel);
    }
}
