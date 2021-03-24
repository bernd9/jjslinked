package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityCrudHandler;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.ReferencedFieldHandler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;;

@RequiredArgsConstructor
class EntityCrudHandlerWriter {
    private final EntityCrudHandlerModel entityCrudHandlerModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
            TypeSpec.Builder builder = TypeSpec.classBuilder(entityCrudHandlerModel.getCrudHandlerSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ParameterizedTypeName.get(ClassName.get(EntityCrudHandler.class),
                        entityTypeName(), entityPkTypeName(), entityProxyTypeName()));

            writeTypeBody(builder);
            TypeSpec typeSpec = builder.build();
            JavaFile javaFile = JavaFile.builder(entityCrudHandlerModel.getCrudHandlerPackageName(), typeSpec)
                    .skipJavaLangImports(true)
                    .build();
            javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        addFieldHandlerTypes(builder);
    }

    private void addFieldHandlerTypes(TypeSpec.Builder builder) {
       // addReferencedFieldHandlerTypes(builder);
    }

    private void addReferencedFieldHandlerTypes(TypeSpec.Builder builder) {
        entityCrudHandlerModel.getEntityModel().getReferredFields().stream()
                .map(this::getReferencedFieldHandler)
                .forEach(builder::addType);
    }

    private TypeSpec getReferencedFieldHandler(ReferredFieldModel fieldModel) {
        return TypeSpec.classBuilder(entityCrudHandlerModel.getReferencedFieldHandlerInnerClassName(fieldModel))
                .superclass(ParameterizedTypeName.get(ClassName.get(ReferencedFieldHandler.class),
                       entityPkTypeName(),
                        fieldEntityTypeName(fieldModel),
                        fieldEntityPkTypeName(fieldModel),
                        fieldEntityProxyTypeName(fieldModel)))
                .addMethod(MethodSpec.constructorBuilder()
                        .addStatement("super(new $T(), \"$L\")", fieldEntityTableAccessor(), fieldModel.getColumnName())
                        .build())
                .build();
    }

    private TypeName fieldEntityTypeName(ReferredFieldModel fieldModel) {
        return TypeName.get(fieldModel.getFieldEntityModel().getType().asType());
    }

    private TypeName fieldEntityProxyTypeName(ReferredFieldModel fieldModel) {
        return EntityProxyModel.getEntityProxyTypeName(fieldModel.getFieldEntityModel());
    }

    private TypeName fieldEntityPkTypeName(ReferredFieldModel fieldModel) {
        return TypeName.get(fieldModel.getFieldEntityModel().getIdField().getFieldType());
    }

    private void addConstructor(TypeSpec.Builder builder) {
        TypeName entityTableAccessor = EntityTableAccessorModel.getEntityTableAccessorTypeName(entityModel());
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("super(new $T())", entityTableAccessor)
                .build());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }


    private TypeName entityPkTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private EntityModel entityModel() {
        return entityCrudHandlerModel.getEntityModel();
    }

    private TypeName entityProxyTypeName() {
        return entityCrudHandlerModel.getEntityProxyTypeName();
    }

    private TypeName fieldEntityTableAccessor() {
        return EntityTableAccessorModel.getEntityTableAccessorTypeName(entityModel());
    }

}
