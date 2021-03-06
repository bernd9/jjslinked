package one.xis.processor;

import one.xis.util.FieldUtils;
import one.xis.util.JavaModelUtils;
import one.xis.util.StringUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityResultSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@RequiredArgsConstructor
public class EntityResultSetWriter {
    private final EntityResultSetModel resultSetModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(resultSetModel.getSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(superclass())
                .addOriginatingElement(resultSetModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(resultSetModel.getPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private TypeName superclass() {
        return ParameterizedTypeName.get(ClassName.get(EntityResultSet.class),
                entityTypeName(),
                entityPkTypeName(),
                entityProxyTypeName());
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
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("getEntityProxy")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityProxyTypeName())
                .addException(SQLException.class)
                .addStatement("$T entity = new $T()", entityProxyTypeName(), entityProxyTypeName());
        addSetNonComplexValuesGetEntityCode(builder);
        return builder.addStatement("return entity").build();
    }


    private void addSetNonComplexValuesGetEntityCode(MethodSpec.Builder builder) {
        for (SimpleEntityFieldModel fieldModel : sortedByColumnName(resultSetModel.getEntityModel().getNonComplexFields())) {
            fieldModel.getSetter().ifPresentOrElse(setter -> addNonComplexSetterExecutionGetEntity(setter, fieldModel, builder),
                    () -> addSettingNonComplexFieldValueGetEntity(fieldModel, builder)); // TODO test for field-setter
        }
    }

    private void addNonComplexSetterExecutionGetEntity(ExecutableElement setter, FieldModel fieldModel, MethodSpec.Builder builder) {
        builder.addStatement("entity.$L($L(\"$L\"))", setter.getSimpleName(), getResultGetter(fieldModel), fieldModel.getColumnName());
    }

    private void addSettingNonComplexFieldValueGetEntity(FieldModel fieldModel, MethodSpec.Builder builder) {
        builder.addStatement("$T.setFieldValue(entity, \"$L\", $L(\"$L\"))", FieldUtils.class, fieldModel.getFieldName(),
                getResultGetter(fieldModel), fieldModel.getColumnName());
    }

    private String getResultGetter(FieldModel fieldModel) {
        String simpleName = JavaModelUtils.getSimpleName(fieldModel.getField().asType());
        String name = String.format("get_%s", StringUtils.firstToUpperCase(simpleName));
        if (EntityResultSetModel.getResultGetters().contains(name)) {
            return name;
        }
        if (JavaModelUtils.isByteArray(fieldModel.getFieldType())) {
            return "get_bytes"; // TODO add this to validation, too
        }
        throw new IllegalStateException("should be caught in validation"); // TODO
    }

    private EntityModel entityModel() {
        return resultSetModel.getEntityModel();
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }

    private TypeName entityProxyTypeName() {
        return EntityProxyModel.getEntityProxyTypeName(entityModel());
    }


    private String entityProxyClassName() {
        return EntityProxyModel.getEntityProxySimpleName(entityModel());
    }

    private TypeName entityPkTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }


    private <T extends SimpleEntityFieldModel> List<T> sortedByColumnName(Collection<T> values) {
        List<T> rv = new ArrayList<>(values);
        Collections.sort(rv, Comparator.comparing(SimpleEntityFieldModel::getColumnName));
        return rv;
    }
}
