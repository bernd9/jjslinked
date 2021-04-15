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
                        fieldPkTypeName()));
        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(model.getCrossTableAccessorPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        //System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        builder.addMethod(implementSetFieldKey());
        builder.addMethod(implementSetEntityKey());
    }

    private void addConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("super(new $T(\"$L\",\"$L\",\"$L\"))",
                        TypeName.get(CrossTableStatements.class),
                        model.getCrossTable(),
                        model.getEntityColumnNameInCrossTable(),
                        model.getFieldColumnNameInCrossTable())
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

    private TypeName fieldPkTypeName() {
        return TypeName.get(model.getFieldKeyType());
    }
}
