package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityResultSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class EntityResultSetWriter {
    private final EntityResultSetModel resultSetModel;
    private final ProcessingEnvironment processingEnvironment;

    private static Set<String> resultGetters;
    static {
        resultGetters = findResultGetters();
    }

    private static  Set<String> findResultGetters() {
        Set<String> names = new HashSet<>();
        names.addAll(findResultGetters(ResultSet.class));
        names.addAll(findResultGetters(EntityResultSet.class));
        return names;
    }

    private static Set<String> findResultGetters(Class<? extends ResultSet>  resultSetClass) {
        return Arrays.stream(EntityResultSet.class.getDeclaredMethods())
                .filter(m -> m.getParameters().length == 1)
                .filter(m -> m.getParameters()[0].getType() == int.class)
                .filter(m -> m.getName().matches("get.*(Object)?"))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

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
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("getEntity")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityTypeName())
                .addException(SQLException.class)
                .addStatement("$T entity = new $T()", entityTypeName(), entityTypeName());
        for (SimpleEntityFieldModel fieldModel : resultSetModel.getEntityModel().getNonComplexFields().values()) {


        }
        return builder.addStatement("return entity").build();
    }

    private Optional<String> getResultGetter(TypeMirror type) {
        String name = String.format("get%s", type.toString());
        if (resultGetters.contains(name)) {
          return Optional.of(name);
        }
        name = String.format("get%sObject", type.toString());
        if (resultGetters.contains(name)) {
            return Optional.of(name);
        }
        return Optional.empty();

    }


    private EntityModel entityModel() {
        return resultSetModel.getEntityModel();
    }


    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }


}
