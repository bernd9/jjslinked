package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.Id;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.PreparedEntityStatement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@RequiredArgsConstructor
public class EntityTableAccessorWriter {
    private final EntityTableAccessorModel accessorModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(accessorModel.getEntityTableAccessorSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(EntityTableAccessor.class),
                        entityTypeName(), entityPkTypeName(), entityProxyTypeName()))
                .addOriginatingElement(accessorModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(accessorModel.getEntityTableAccessorPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();

        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createConstructor(builder);
        implementAbstractMethods(builder);
    }

    private void createConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(new $L(), $T.class)", EntityStatementsModel.getEntityStatementsSimpleName(entityModel()), entityPkType())
                .build());
    }

    private void implementAbstractMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementInsertSingleEntityProxy());
        builder.addMethod(implementInsertEntityCollection());
        builder.addMethod(implementToEntityProxy());
        builder.addMethod(implementGetPkEntity());
        builder.addMethod(implementGetPkResultSet());
        builder.addMethod(implementSetPkEntity());
        builder.addMethod(implementSetPkStatement());
        builder.addMethod(implementGenerateKey());
    }


    private MethodSpec implementInsertSingleEntityProxy() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityTypeName(), "entity");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API:
                builder.addStatement("insertWithApiGeneratedKey(entity)");
                break;
            case DBMS:
                builder.addStatement("insertWithDbmsGeneratedKey(entity)");
                break;
            case NONE:
                builder.addStatement("insertWithManuallyPlacedKey(entity)");
                break;
        }
        return builder.build();
    }

    private MethodSpec implementInsertEntityCollection() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityCollectionTypeName(), "entities");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API:
                builder.addStatement("insertWithApiGeneratedKeys(entities)");
                break;
            case DBMS:
                builder.addStatement("insertWithDbmsGeneratedKeys(entities)");
                break;
            case NONE:
                builder.addStatement("insertWithManuallyPlacedKeys(entities)");
                break;
        }
        return builder.build();
    }

    private MethodSpec implementToEntityProxy() {
        return MethodSpec.methodBuilder("toEntityProxy")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(entityProxyTypeName())
                .addException(SQLException.class)
                .addParameter(TypeName.get(ResultSet.class), "rs")
                .addStatement("return new $L(rs).getEntityProxy()", EntityResultSetModel.getSimpleName(entityModel()))
                .build();
    }

    private MethodSpec implementSetPkEntity() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityTypeName(), "entity")
                .addParameter(entityPkTypeName(), "pk")
                .addStatement("$L.setPk(entity, pk)", EntityUtilModel.getEntityUtilSimpleName(entityModel()))
                .build();
    }

    private MethodSpec implementSetPkStatement() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(PreparedEntityStatement.class), "st")
                .addParameter(TypeName.INT, "index")
                .addParameter(entityPkTypeName(), "pk")
                .addStatement("st.set(index, pk)")
                .build();
    }

    private MethodSpec implementGetPkEntity() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(entityPkTypeName())
                .addParameter(entityTypeName(), "entity")
                .addStatement("return $L.getPk(entity)", EntityUtilModel.getEntityUtilSimpleName(entityModel()))
                .build();
    }

    private MethodSpec implementGetPkResultSet() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addException(SQLException.class)
                .returns(entityPkTypeName())
                .addParameter(TypeName.get(ResultSet.class), "rs")
                .addParameter(TypeName.INT, "index")
                .addStatement("return new $L(rs).get_$L(index)", EntityResultSetModel.getSimpleName(entityModel()), JavaModelUtils.getSimpleName(entityPkType()))
                .build();
    }

    // TODO : has to overridden for api-generated key only, otherwise throw an AbstractMethodError
    private MethodSpec implementGenerateKey() {
        return MethodSpec.methodBuilder("generateKey")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(entityPkTypeName())
                .addStatement("throw new $T()", TypeName.get(AbstractMethodError.class))
                .build();
    }


    private EntityModel entityModel() {
        return accessorModel.getEntityModel();
    }

    private ParameterizedTypeName entityCollectionTypeName() {
        return ParameterizedTypeName.get(ClassName.get(Collection.class), entityTypeName());
    }

    private TypeName entityProxyTypeName() {
        String type = new StringBuilder()
                .append(entityModel().getPackageName())
                .append(".")
                .append(EntityProxyModel.getEntityProxySimpleName(entityModel()))
                .toString();
        return TypeName.get(processingEnvironment.getElementUtils().getTypeElement(type).asType());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }

    private TypeName entityPkTypeName() {
        return TypeName.get(entityPkType());
    }

    private TypeMirror entityPkType() {
        return entityModel().getIdField().getFieldType();
    }
}
