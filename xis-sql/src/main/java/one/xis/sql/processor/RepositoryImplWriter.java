package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.RepositoryBase;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

@RequiredArgsConstructor
public class RepositoryImplWriter {
    private final RepositoryImplModel repositoryImplModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(repositoryImplModel.getClassName())
                .superclass(ParameterizedTypeName.get(ClassName.get(RepositoryBase.class), entityTypeName(), entityPkType()))
                .addMethod(constructor());
        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(repositoryImplModel.getRepositoryImplPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addStatement("super(new $T())", repositoryImplModel.getCrudHandlerTypeName())
                .build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {

    }

    private TypeName entityTypeName() {
        return entityModel().getTypeName();
    }

    private TypeName entityPkType() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private EntityModel entityModel() {
        return repositoryImplModel.getEntityModel();
    }

}
