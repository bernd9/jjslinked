package one.xis.sql.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;


class EntityProxyWriter {
    private final EntityModel entityModel;
    private final ProcessingEnvironment processingEnvironment;
    private final EntityCollections entityCollections;

    EntityProxyWriter(EntityModel entityModel, ProcessingEnvironment processingEnvironment) {
        this.entityModel = entityModel;
        this.processingEnvironment = processingEnvironment;
        this.entityCollections = new EntityCollections(processingEnvironment);
    }

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityModel.getSimpleName() + "Proxy")
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
        addEditedFlagField(builder);
        addGetters(builder);
        addSetters(builder);
    }

    private void addEditedFlagField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, "edited", Modifier.PUBLIC).build());
    }

    private void addWrappedEntityField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(entityTypeName(), "entity", Modifier.PRIVATE, Modifier.FINAL).build());
    }

    private void addGetters(TypeSpec.Builder builder) {
        entityModel.getEntityFields().stream()
                .filter(field -> field.getGetter().isPresent())
                .forEach(field -> addGetter(field, builder));
    }

    private void addGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel.isEntityField()) {
            addEntityGetter(fieldModel, builder);
        } else {
            addSimpleGetter(fieldModel, builder);
        }
    }

    private void addSimpleGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    private void addEntityGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel.isCollection()) {
            addEntityCollectionGetter(fieldModel, builder);
        } else {
            addSingleEntityGetter(fieldModel, builder);
        }
    }

    private void addSingleEntityGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("if (super.$L() == null)")
                .addStatement("")
                .addStatement("")
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    private void addEntityCollectionGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
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
        if (fieldModel.isEntityField()) {
            addEntitySetter(fieldModel, builder);
        } else {
            addSimpleSetter(fieldModel, builder);
        }
    }

    private void addEntitySetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel.isCollection()) {
            addEntityCollectionSetter(fieldModel, builder);
        } else {
            addSingleEntitySetter(fieldModel, builder);
        }
    }

    private void addSimpleSetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("entity.$L($T $l)", setter.getSimpleName(), parameterTypeName(setter, 0), "value")
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
    }

    private void addSingleEntitySetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("value = new $T<>(value)", entityCollections.getCollectionWrapperType(fieldModel.getFieldType()))
                .addStatement("entity.$L(value)", setter.getSimpleName())
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
    }

    // TODO validate collection must have a generic type
    private void addEntityCollectionSetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("value = new $T<>(value)", entityCollections.getCollectionWrapperType(fieldModel.getFieldType()))
                .addStatement("entity.$L(value)", setter.getSimpleName())
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel.getType().asType());
    }

    private static TypeName parameterTypeName(ExecutableElement method, int paramIndex) {
        return TypeName.get(method.getParameters().get(paramIndex).asType());
    }
}
