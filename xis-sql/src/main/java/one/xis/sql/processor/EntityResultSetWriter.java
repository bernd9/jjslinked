package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityResultSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


@RequiredArgsConstructor
public class EntityResultSetWriter {
    private final EntityResultSetModel resultSetModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(resultSetModel.getSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(EntityResultSet.class), entityTypeName()))
                .addOriginatingElement(resultSetModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(resultSetModel.getPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        implementAbstractMethods(builder);

    }

    private void addConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(ResultSet.class, "resultSet").build())
                .addStatement("super(resultSet)")
                .build());
    }

    private void implementAbstractMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementGetEntity());
    }

    private MethodSpec implementGetEntity() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEntity")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityTypeName())
                .addException(SQLException.class)
                .addStatement("$T entity = new $T()", entityTypeName(), entityTypeName());
        for (SimpleEntityFieldModel fieldModel : resultSetModel.getEntityModel().getNonComplexFields().values()) {

        }
        return builder.addStatement("return entity").build();
    }


    private EntityModel entityModel() {
        return resultSetModel.getEntityModel();
    }


    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }


}
