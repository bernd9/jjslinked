package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityCrudHandler;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;;

@RequiredArgsConstructor
class EntityCrudHandlerWriter {
    private final EntityCrudHandlerModel entityCrudHandlerModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
            TypeSpec.Builder builder = TypeSpec.classBuilder(entityCrudHandlerModel.getCrudHandlerSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ParameterizedTypeName.get(ClassName.get(EntityCrudHandler.class),
                        entityTypeName(), entityIdTypeName(), entityProxyTypeName()));

            writeTypeBody(builder);
            TypeSpec typeSpec = builder.build();
            JavaFile javaFile = JavaFile.builder(entityCrudHandlerModel.getCrudHandlerPackageName(), typeSpec)
                    .skipJavaLangImports(true)
                    .build();
            javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        addFieldHandlers(builder);
    }

    private void addFieldHandlers(TypeSpec.Builder builder) {

    }

    private void addConstructor(TypeSpec.Builder builder) {
        TypeName entityTableAccessor = EntityTableAccessorModel.getEntityTableAccessorTypeName(entityModel());
        TypeName entityStatements = EntityStatementsModel.getEntityStatementsTypeName(entityModel());
        TypeMirror pkType = entityModel().getIdField().getFieldType();
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("super(new $T())", entityTableAccessor)
                .build());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }


    private TypeName entityIdTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private EntityModel entityModel() {
        return entityCrudHandlerModel.getEntityModel();
    }

    private TypeName entityProxyTypeName() {
        return entityCrudHandlerModel.getEntityProxyTypeName();
    }

}
