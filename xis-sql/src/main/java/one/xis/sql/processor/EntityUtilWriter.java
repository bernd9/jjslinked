package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
class EntityUtilWriter {
    private final EntityUtilModel entityUtilModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassName.OBJECT)
                .addModifiers(Modifier.PUBLIC)
                .addOriginatingElement(entityUtilModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(entityUtilModel.getEntityModel().getPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();

        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createConstructor(builder);
        builder.addMethod(implementGetPk());
        builder.addMethod(implementSetPk());
    }

    private void createConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());
    }

    private MethodSpec implementGetPk() {
        return entityUtilModel.getEntityModel().getIdField().getGetter()
                .map(this::implementGetPkWithGetter)
                .orElseGet(this::implementGetPkWithFieldAccess);
    }


    private MethodSpec implementGetPkWithGetter(ExecutableElement getter) {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityUtilModel.getEntityModel().getTypeName(), "entity")
                .addStatement("return entity.$L()", getter.getSimpleName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private MethodSpec implementGetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addStatement("return $T.getFieldValue(entity, \"$L\")", FieldUtils.class, pkField().getFieldName())
                .returns(pkType())
                .build();
    }

    private MethodSpec implementSetPk() {
        return entityUtilModel.getEntityModel().getIdField().getSetter()
                .map(this::implementSetPkWithSetter)
                .orElseGet(this::implementSetPkWithFieldAccess);
    }


    private MethodSpec implementSetPkWithSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("entity.$L(pk)", setter.getSimpleName())
                .build();
    }

    private MethodSpec implementSetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("return $T.setFieldValue(entity, \"$L\", pk)", FieldUtils.class, pkField().getFieldName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private TypeName entityType() {
        return entityUtilModel.getEntityModel().getTypeName();
    }

    private TypeName pkType() {
        return TypeName.get(pkField().getFieldType());
    }

    private SimpleEntityFieldModel pkField() {
        return entityUtilModel.getEntityModel().getIdField();
    }


}
