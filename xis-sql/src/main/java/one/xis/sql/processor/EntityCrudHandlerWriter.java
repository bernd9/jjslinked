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
                        entityTypeName(), entityPkTypeName()));

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(entityCrudHandlerModel.getCrudHandlerPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        //System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        addFieldHandlerTypes(builder);
        addFieldHandlerFields(builder);
        builder.addMethod(implementSaveMethod());
    }

    private MethodSpec implementSaveMethod() {
        return new DoSaveMethodBuilder().implementSaveMethod();
    }

    private void addFieldHandlerTypes(TypeSpec.Builder builder) {
        addReferredFieldHandlerTypes(builder);
    }

    private void addFieldHandlerFields(TypeSpec.Builder builder) {
        addForeignKeyFieldHandlerFields(builder);
        addReferredFieldHandlerFields(builder);
    }


    private void addReferredFieldHandlerTypes(TypeSpec.Builder builder) {
        sortedReferencedFieldModels()
                .map(this::getReferencedFieldHandlerTypeSpec)
                .forEach(builder::addType);
    }

    private void addForeignKeyFieldHandlerFields(TypeSpec.Builder builder) {
        sortedForeignKeyFieldModels()
                .map(this::getForeignKeyCrudHandlerField)
                .forEach(builder::addField);
    }


    private void addReferredFieldHandlerFields(TypeSpec.Builder builder) {
        sortedReferencedFieldModels()
                .map(this::getReferencedFieldHandlerField)
                .forEach(builder::addField);
    }

    private Stream<ReferencedFieldModel> sortedReferencedFieldModels() {
        return entityCrudHandlerModel.getEntityModel().getReferredFields().stream()
                .sorted(Comparator.comparing(FieldModel::getColumnName));
    }

    private Stream<ForeignKeyFieldModel> sortedForeignKeyFieldModels() {
        return entityCrudHandlerModel.getEntityModel().getForeignKeyFields().stream()
                .sorted(Comparator.comparing(FieldModel::getColumnName));
    }

    private TypeSpec getReferencedFieldHandlerTypeSpec(ReferencedFieldModel fieldModel) {
        return new ReferencedFieldHandlerTypeBuilder(fieldModel).fieldHandlerDeclaration();
    }

    private FieldSpec getForeignKeyCrudHandlerField(ForeignKeyFieldModel fieldModel) {
        ClassName crudHandlerType = fieldModel.getCrudHandlerName();
        String crudHandlerFieldName = StringUtils.firstToLowerCase(crudHandlerType.simpleName());
        return FieldSpec.builder(crudHandlerType, crudHandlerFieldName, Modifier.PRIVATE, Modifier.STATIC)
                .initializer(CodeBlock.builder().add("new $L()", crudHandlerType).build())
                .build();
    }

    private FieldSpec getReferencedFieldHandlerField(ReferencedFieldModel fieldModel) {
        String fieldHandlerName = fieldModel.getFieldHandlerName();
        String fieldHandlerFieldName = StringUtils.firstToLowerCase(fieldModel.getFieldHandlerName());
        return getFieldHandlerField(fieldHandlerFieldName, fieldHandlerName);
    }

    private FieldSpec getFieldHandlerField(String fieldHandlerFieldName, String fieldHandlerName) {
        ClassName crudHandlerClassName = entityCrudHandlerModel.getCrudHandlerTypeName(entityModel());
        ClassName fieldHandlerInnerClassName = crudHandlerClassName.nestedClass(fieldHandlerName);
        return FieldSpec.builder(fieldHandlerInnerClassName, fieldHandlerFieldName, Modifier.PRIVATE, Modifier.STATIC)
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

    private class DoSaveMethodBuilder {
        private final MethodSpec.Builder builder;

        DoSaveMethodBuilder() {
            builder = MethodSpec.methodBuilder("doSave")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PROTECTED)
                    .addParameter(ParameterSpec.builder(entityTypeName(), "entity").build());
        }

        MethodSpec implementSaveMethod() {
            addForeignKeyFieldCrudHandlerCalls();
            addSaveEntityStatement();
            addReferencedFieldHandlerCalls();
            return builder.build();
        }

        private void addForeignKeyFieldCrudHandlerCalls() {
            sortedForeignKeyFieldModels().forEach(this::addForeignKeyFieldCrudHandlerCall);
        }

        private void addForeignKeyFieldCrudHandlerCall(ForeignKeyFieldModel foreignkeyFieldModel) {
            String handlerInstanceFieldName = StringUtils.firstToLowerCase(foreignkeyFieldModel.getCrudHandlerName().simpleName());
            TypeName entityUtilTypeName = EntityUtilModel.getEntityUtilTypeName(entityModel());
            String fieldValueGetterName = EntityUtilModel.getGetterName(foreignkeyFieldModel.getFieldName().toString());
            builder.addStatement("$L.save($T.$L(entity))", handlerInstanceFieldName, entityUtilTypeName, fieldValueGetterName);
        }

        private void addSaveEntityStatement() {
            builder.addStatement("getEntityTableAccessor().save(entity)");
        }

        private void addReferencedFieldHandlerCalls() {
            sortedReferencedFieldModels().forEach(this::addReferencedFieldHandlerCall);
        }

        private void addReferencedFieldHandlerCall(ReferencedFieldModel referencedFieldModel) {
            String handlerInstanceFieldName = StringUtils.firstToLowerCase(referencedFieldModel.getFieldHandlerName());
            TypeName entityUtilTypeName = EntityUtilModel.getEntityUtilTypeName(entityModel());
            String fieldValueGetterName = EntityUtilModel.getGetterName(referencedFieldModel.getFieldName().toString());
            builder.addStatement("$L.updateFieldValues(entity, $T.$L(entity))", handlerInstanceFieldName, entityUtilTypeName, fieldValueGetterName);
        }
    }

}
