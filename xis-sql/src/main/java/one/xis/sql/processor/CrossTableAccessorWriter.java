package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.CrossTableAccessor;
import one.xis.sql.api.CrossTableStatements;
import one.xis.sql.api.JdbcStatement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
class CrossTableAccessorWriter {
    private final CrossTableAccessorModel model;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(model.getCrossTableAccessorSimpleName())
                .superclass(ParameterizedTypeName.get(ClassName.get(CrossTableAccessor.class),
                        entityPkTypeName(),
                        fieldEntityTypeName(),
                        fieldPkTypeName()));
        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(model.getCrossTableAccessorPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createInstanceField(builder);
        addConstructor(builder);
        createGetInstanceMethod(builder);
        builder.addMethod(implementSetFieldKey());
        builder.addMethod(implementSetEntityKey());
    }

    private void createInstanceField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(model.getCrossTableAccessorClassName(), "instance")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", model.getCrossTableAccessorClassName())
                .build());
    }

    private void createGetInstanceMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("return instance")
                .returns(model.getCrossTableAccessorClassName())
                .build());
    }


    private void addConstructor(TypeSpec.Builder builder) {
        EntityModel fieldEntityModel = model.getCrossTableFieldModel().getFieldEntityModel();
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("super(new $T(\"$L\",\"$L\",\"$L\", new $T()), new $T())",
                        TypeName.get(CrossTableStatements.class),
                        model.getCrossTable(),
                        model.getEntityColumnNameInCrossTable(),
                        model.getFieldColumnNameInCrossTable(),
                        EntityStatementsModel.getEntityStatementsTypeName(fieldEntityModel),
                        EntityFunctionsModel.getEntityFunctionsTypeName(fieldEntityModel)
                )
                .build());
    }

    private MethodSpec implementSetFieldKey() {
        return MethodSpec.methodBuilder("setFieldKey")
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(JdbcStatement.class), "st")
                .addParameter(TypeName.INT, "index")
                .addParameter(fieldPkTypeName(), "fieldPk")
                .addStatement("st.set(index, fieldPk)")
                .build();
    }

    private MethodSpec implementSetEntityKey() {
        return MethodSpec.methodBuilder("setEntityKey")
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(JdbcStatement.class), "st")
                .addParameter(TypeName.INT, "index")
                .addParameter(fieldPkTypeName(), "entityPk")
                .addStatement("st.set(index, entityPk)")
                .build();
    }

    private TypeName entityPkTypeName() {
        return TypeName.get(model.getEntityKeyType());
    }

    private TypeName fieldEntityTypeName() {
        return model.getCrossTableFieldModel().getFieldEntityModel().getTypeName();
    }

    private TypeName fieldPkTypeName() {
        return TypeName.get(model.getFieldKeyType());
    }
}
