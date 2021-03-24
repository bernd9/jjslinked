package one.xis.sql.processor;

import com.ejc.util.StringUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityCrudHandler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;;

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
            StringBuilder s = new StringBuilder();
            javaFile.writeTo(s);
            System.out.println(s);
            javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        addFieldHandlerTypes(builder);
        addFieldHandlerFields(builder);
        builder.addMethod(implementSaveMethod());
    }

    private MethodSpec implementSaveMethod() {
        return new SaveMethodBuilder().implementSaveMethod();
    }



    private void addFieldHandlerTypes(TypeSpec.Builder builder) {
       addReferredFieldHandlerTypes(builder);
    }

    private void addFieldHandlerFields(TypeSpec.Builder builder) {
        sortedReferencedFieldModels()
                .map(this::getReferencedFieldHandlerTypeSpec)
                .forEach(builder::addType);
    }

    private void addReferredFieldHandlerTypes(TypeSpec.Builder builder) {
        sortedReferencedFieldModels()
                .map(this::getReferencedFieldHandlerField)
                .forEach(builder::addField);
    }

    private Stream<ReferencedFieldModel> sortedReferencedFieldModels() {
        return entityCrudHandlerModel.getEntityModel().getReferredFields().stream()
                .sorted(Comparator.comparing(ReferencedFieldModel::getColumnName));
    }

    private TypeSpec getReferencedFieldHandlerTypeSpec(ReferencedFieldModel fieldModel) {
       return new ReferencedFieldHandlerTypeBuilder(fieldModel).fieldHandlerDeclaration();
    }

    private FieldSpec getReferencedFieldHandlerField(ReferencedFieldModel fieldModel) {
        String fieldHandlerFieldName = StringUtils.firstToLowerCase(fieldModel.getFieldHandlerName());
        ClassName crudHandlerClassName = entityCrudHandlerModel.getCrudHandlerTypeName(entityModel());
        ClassName fieldHandlerInnerClassName = crudHandlerClassName.nestedClass(fieldModel.getFieldHandlerName());
        return FieldSpec.builder(fieldHandlerInnerClassName,  fieldHandlerFieldName, Modifier.PRIVATE, Modifier.STATIC)
                .initializer(CodeBlock.builder().add("new $L()", fieldHandlerInnerClassName).build())
                .build();

    }

    private TypeName fieldEntityTypeName(ReferencedFieldModel fieldModel) {
        return TypeName.get(fieldModel.getFieldEntityModel().getType().asType());
    }

    private TypeName fieldEntityProxyTypeName(ReferencedFieldModel fieldModel) {
        return EntityProxyModel.getEntityProxyTypeName(fieldModel.getFieldEntityModel());
    }

    private TypeName fieldEntityPkTypeName(ReferencedFieldModel fieldModel) {
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

    private class SaveMethodBuilder {
        private final MethodSpec.Builder builder;

        SaveMethodBuilder() {
            builder = MethodSpec.methodBuilder("save")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(entityTypeName(), "entity").build());
        }

        MethodSpec implementSaveMethod() {
            addReferencedFieldHandlerCalls();
            return builder.build();
        }

        private void addReferencedFieldHandlerCalls() {
            sortedReferencedFieldModels().forEach(this::addReferencedFieldHandlerCall);
        }

        private void addReferencedFieldHandlerCall(ReferencedFieldModel referencedFieldModel) {
            String handlerInstanceFieldName = StringUtils.firstToLowerCase(referencedFieldModel.getFieldHandlerName());
            TypeName entityUtilTypeName = EntityUtilModel.getEntityUtilTypeName(entityModel());
            String fieldValueGetterName = EntityUtilModel.getGetterName(referencedFieldModel.getFieldName().toString());
            builder.addStatement("$L.updateFieldValues($T.getPk(entity), $T.$L(entity))", handlerInstanceFieldName, entityUtilTypeName, entityUtilTypeName, fieldValueGetterName);
        }
    }

}
