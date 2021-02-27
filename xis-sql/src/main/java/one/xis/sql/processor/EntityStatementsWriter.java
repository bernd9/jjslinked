package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityStatements;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.sql.PreparedStatement;

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
        //builder.addMethod(implementSetInsertSqlParameters());
    }

    private MethodSpec implementGetInsertSql() {
        return MethodSpec.methodBuilder("getInsertSql")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addStatement("return \"$L\"", statementsModel.getInsertSql())
                .build();
    }


    private MethodSpec implementSetInsertSqlParameters() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setInsertSqlParameters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(TypeName.get(PreparedStatement.class), "st").build())
                .addParameter(ParameterSpec.builder(TypeName.get(statementsModel.getEntityModel().getType().asType()), "entity").build());

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
