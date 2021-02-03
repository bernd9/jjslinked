package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;

@Getter
class EntityHandlerModel {

    private final EntityModel entityModel;
    private final InsertStatementModel insertStatementModel;
    private final String handlerPackageName;
    private final String handlerSimpleName;

    EntityHandlerModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        this.insertStatementModel = new InsertStatementModel(entityModel);
        this.handlerPackageName = JavaModelUtils.getPackageName(entityModel.getType());
        this.handlerSimpleName = JavaModelUtils.getSimpleName(entityModel.getType()) + "EntityHandler";
    }
}
