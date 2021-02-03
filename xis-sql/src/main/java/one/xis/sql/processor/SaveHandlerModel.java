package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveHandlerModel {
    private final EntityModel entityModel;

    String getSimpleName() {
        return entityModel.getType().getSimpleName() + "SaveHandler";
    }

    String getPackageName() {
        return JavaModelUtils.getPackageName(entityModel.getType());
    }

}
