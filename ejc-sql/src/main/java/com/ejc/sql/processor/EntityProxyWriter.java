package com.ejc.sql.processor;

import com.ejc.javapoet.JavaWriter;
import com.ejc.sql.api.entity.EntityProxy;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import java.util.Optional;

import static java.util.Collections.singleton;

class EntityProxyWriter extends JavaWriter {

    private final EntityModel entityModel;
    private static final String FIELD_NAME_EDITED = "edited";
    private static final String FIELD_NAME_ENTITY = "entity";

    EntityProxyWriter(String simpleName, Optional<String> packageName, Optional<TypeName> superClass, ProcessingEnvironment processingEnvironment, EntityModel entityModel) {
        super(simpleName, packageName, superClass, processingEnvironment, singleton(EntityProxy.class));
        this.entityModel = entityModel;
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {
        writeWrappedEntityField(builder);
        writeUpdatedField(builder);
        writeEntityGetter(builder);
        writeIsEditedMethod(builder);
        writeSimpleFieldSetters(builder);
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addParameter(ParameterSpec.builder(TypeName.get(entityModel.getEntityType().asType()), FIELD_NAME_ENTITY).build())
                .addStatement("this.$L = $L", FIELD_NAME_ENTITY, FIELD_NAME_ENTITY);
    }

    private void writeEntityGetter(TypeSpec.Builder builder) {
        builder.addMethod(entityGetter());
    }

    private void writeIsEditedMethod(TypeSpec.Builder builder) {
        builder.addMethod(isEdited());
    }

    private MethodSpec entityGetter() {
        return MethodSpec.methodBuilder("getEntity")
                .returns(TypeName.OBJECT)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", FIELD_NAME_ENTITY)
                .build();
    }

    private MethodSpec isEdited() {
        return MethodSpec.methodBuilder("isEdited")
                .returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", FIELD_NAME_EDITED)
                .build();
    }


    private void writeUpdatedField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, FIELD_NAME_EDITED, Modifier.PUBLIC).build()).build();
    }

    private void writeWrappedEntityField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.get(entityModel.getEntityType().asType()), FIELD_NAME_ENTITY, Modifier.PUBLIC).build()).build();
    }

    private void writeSimpleFieldSetters(TypeSpec.Builder builder) {
        this.entityModel.getSimpleFields()
                .stream()
                .filter(entityField -> entityField.getSetter().isPresent())
                .forEach(entityField -> writeSimpleFieldSetter(entityField.getSetter().get(), builder));
    }

    private void writeSimpleFieldSetter(ExecutableElement setter, TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.overriding(setter)
                .addStatement(createSuperMethodCall(setter))
                .addStatement("$L = $L", FIELD_NAME_EDITED, true).build());
    }

    private static CodeBlock createSuperMethodCall(ExecutableElement method) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if (method.getReturnType().getKind() != TypeKind.VOID) {
            codeBlockBuilder.add("return ");
        }
        codeBlockBuilder
                .add("super.$L(", method.getSimpleName())
                .add(JavaModelUtils.parameterNameList(method))
                .add(")");
        return codeBlockBuilder.build();
    }

}
