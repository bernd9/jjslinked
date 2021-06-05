package one.xis.processor;

import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;

@Getter
@RequiredArgsConstructor
class RepositoryImplModel {
    @Getter
    private final EntityModel entityModel;
    private final TypeElement repositoryInterface;

    ClassName getClassName() {
        return ClassName.get(getRepositoryImplPackageName(), getRepositoryImplSimpleName());
    }

    String getRepositoryImplSimpleName() {
        return JavaModelUtils.getSimpleName(repositoryInterface) + "Impl";
    }

    String getRepositoryImplPackageName() {
        return JavaModelUtils.getPackageName(repositoryInterface);
    }

    TypeName getCrudHandlerTypeName() {
        return EntityCrudHandlerModel.getCrudHandlerTypeName(entityModel);
    }

}
