package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
class EntityImplWriter {
    private final EntityModel entityModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityModel.getSimpleName()+"Impl")
                .addModifiers(Modifier.DEFAULT)
                .superclass(entityModel.getType().asType())
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(entityModel.getPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.DEFAULT)
                .addParameter(ParameterSpec.builder(entityTypeName(), "entity").build())
                .addStatement("this.entity = entity")
                .build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addWrappedEntityField(builder);
        addGetters(builder);
        addSetters(builder);
        addForeignKeyFields(builder);

    }

    private void addWrappedEntityField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(entityTypeName(), "entity", Modifier.PRIVATE, Modifier.FINAL).build());
    }

    private void addForeignKeyFields(TypeSpec.Builder builder) {
        entityModel.getEntityFields().stream()
                .filter(EntityFieldModel::isForeignKey)
                .forEach(fieldModel -> addForeignKeyField(fieldModel, builder));
    }

    private void addForeignKeyField(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
       builder.addField(FieldSpec.builder(TypeName.get(fieldModel.getFieldType()), foreignKeyFieldName(fieldModel), Modifier.PRIVATE).build());
    }

    private void addGetters(TypeSpec.Builder builder) {
        entityModel.getEntityFields().stream()
            .map(EntityFieldModel::getGetter)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(getter -> addGetter(getter, builder));
    }

    private void addGetter(ExecutableElement getter, TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    private void addSetters(TypeSpec.Builder builder) {
        entityModel.getEntityFields().stream()
                .filter(field -> field.getSetter().isPresent())
                .forEach(field -> addSetter(field, builder));
    }

    private void addSetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString());
        methodBuilder.addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build());
        methodBuilder.addStatement("entity.$L($T $l)", setter.getSimpleName(), parameterTypeName(setter, 0), "value");
        if (fieldModel.isForeignKey()) {

        }
        builder.addMethod(methodBuilder.build());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel.getType().asType());
    }

    private static TypeName parameterTypeName(ExecutableElement method, int paramIndex) {
        return TypeName.get(method.getParameters().get(paramIndex).asType());
    }

    private static String foreignKeyFieldName(EntityFieldModel fieldModel) {
        return fieldModel.getFieldEntityModel().getVarName()+"Id";
    }
}
