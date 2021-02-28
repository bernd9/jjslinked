package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.PreparedEntityStatement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
class EntityStatementsWriter {

    private final EntityStatementsModel statementsModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(statementsModel.getEntityStatementsSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityStatements.class),
                        entityTypeName(), entityIdTypeName()))
                .addOriginatingElement(statementsModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(statementsModel.getEntityStatementsPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        implementMethods(builder);
    }

    private void implementMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementGetInsertSql());
        builder.addMethod(implementGetSelectByIdSql());
        builder.addMethod(implementGetUpdateSql());
        builder.addMethod(implementGetDeleteSql());
        builder.addMethod(implementGetSelectAllSql());
        builder.addMethod(implementGetDeleteAllSql());
        builder.addMethod(implementSetInsertSqlParameters());
        builder.addMethod(implementSetUpdateSqlParameters());
    }

    private MethodSpec implementGetInsertSql() {
        return implementSimpleStringGetter("getInsertSql", statementsModel.getInsertSql());
    }

    private MethodSpec implementGetSelectByIdSql() {
        return implementSimpleStringGetter("getSelectByIdSql", statementsModel.getSelectByIdSql());
    }

    private MethodSpec implementGetUpdateSql() {
        return implementSimpleStringGetter("getUpdateSql", statementsModel.getUpdateSql());
    }

    private MethodSpec implementGetDeleteSql() {
        return implementSimpleStringGetter("getDeleteSql", statementsModel.getDeleteSql());
    }

    private MethodSpec implementGetSelectAllSql() {
        return implementSimpleStringGetter("getSelectAllSql", statementsModel.getSelectAllSql());
    }

    private MethodSpec implementGetDeleteAllSql() {
        return implementSimpleStringGetter("getDeleteAllSql", statementsModel.getDeleteAllSql());
    }


    private MethodSpec implementSimpleStringGetter(String methodName, String returnValue) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addStatement("return \"$L\"", returnValue)
                .build();
    }

    private MethodSpec implementSetInsertSqlParameters() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setInsertSqlParameters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(TypeName.get(PreparedEntityStatement.class), "st").build())
                .addParameter(ParameterSpec.builder(TypeName.get(statementsModel.getEntityModel().getType().asType()), "entity").build());
        int paramIndex = 1;
        for (FieldModel fieldModel : statementsModel.getInsertSqlFields()) {
            if (fieldModel.getGetter().isPresent()) {
                builder.addStatement("st.set($L, entity.$L())", paramIndex++, fieldModel.getGetter().get().getSimpleName());
            } else {
                // TODO test for this case
                builder.addStatement("st.set($L, ($T)$T.getFieldValue(entity, \"$L\"))", paramIndex++, fieldModel.getFieldType(), FieldUtils.class, fieldModel.getFieldName());
            }

        }
        return builder.build();
    }

    private MethodSpec implementSetUpdateSqlParameters() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setUpdateSqlParameters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(TypeName.get(PreparedEntityStatement.class), "st").build())
                .addParameter(ParameterSpec.builder(TypeName.get(statementsModel.getEntityModel().getType().asType()), "entity").build());
        int paramIndex = 1;
        for (FieldModel fieldModel : statementsModel.getUpdateSqlFields()) {
            if (fieldModel.getGetter().isPresent()) {
                builder.addStatement("st.set($L, entity.$L())", paramIndex++, fieldModel.getGetter().get().getSimpleName());
            } else {
                // TODO test for this case
                builder.addStatement("st.set($L, ($T)$T.getFieldValue(entity, \"$L\"))", paramIndex++, fieldModel.getFieldType(), FieldUtils.class, fieldModel.getFieldName());
            }

        }
        return builder.build();
    }


    private TypeVariableName entityTypeVariableName() {
        return TypeVariableName.get("E", TypeName.get(entityModel().getType().asType()));
    }

    private TypeVariableName entityIdTypeVariableName() {
        return TypeVariableName.get("EID", TypeName.get(entityModel().getIdField().getFieldType()));
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }

    private TypeName entityIdTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private EntityModel entityModel() {
        return statementsModel.getEntityModel();
    }
}
