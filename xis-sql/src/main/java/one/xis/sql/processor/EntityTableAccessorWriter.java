package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.Id;
import one.xis.sql.api.EntityProxy;
import one.xis.sql.api.EntityTableAccessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collection;


@RequiredArgsConstructor
public class EntityTableAccessorWriter {
    private final EntityTableAccessorModel accessorModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(accessorModel.getEntityTableAccessorSimpleName())
                .addTypeVariable(entityTypeVariableName())
                .addTypeVariable(entityIdTypeVariableName())
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT) // TODO remove abstract
                .superclass(ParameterizedTypeName.get(ClassName.get(EntityTableAccessor.class),
                        entityTypeName(), entityIdTypeName()))
                .addOriginatingElement(accessorModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(accessorModel.getEntityTableAccessorPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        implementAbstractMethods(builder);
    }

    private void implementAbstractMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementInsertOneEntity());
        builder.addMethod(implementInsertOneEntityCollection());
        builder.addMethod(implementToEntityProxy());
        // TODO
    }

    private MethodSpec implementInsertOneEntity() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityProxyTypeName(), "entityProxy");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API -> builder.addStatement("insertWithApiGeneratedKey(entityProxy)");
            case DBMS -> builder.addStatement("insertWithDbmsGeneratedKey(entityProxy)");
            case NONE -> builder.addStatement("insertWithManuallyPlacedKey(entityProxy) ");
        }
        return builder.build();
    }


    private MethodSpec implementInsertOneEntityCollection() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityProxyCollectionTypeName(), "entityProxies");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API -> builder.addStatement("insertWithApiGeneratedKeys(entityProxies)");
            case DBMS -> builder.addStatement("insertWithDbmsGeneratedKeys(entityProxies)");
            case NONE -> builder.addStatement("insertWithManuallyPlacedKeys(entityProxies) ");
        }
        return builder.build();
    }


    private MethodSpec implementToEntityProxy() {
        return MethodSpec.methodBuilder("toEntityProxy")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(entityProxyTypeName())
                .addParameter(entityTypeName(), "entity")
                .addStatement("return new $L(entity)", accessorModel.getEntityProxySimpleName())
                .build();

    }


    private EntityModel entityModel() {
        return accessorModel.getEntityModel();
    }

    private ParameterizedTypeName entityProxyCollectionTypeName() {
        return ParameterizedTypeName.get(ClassName.get(Collection.class), entityProxyTypeName());
    }


    private ParameterizedTypeName entityProxyTypeName() {
        return ParameterizedTypeName.get(ClassName.get(EntityProxy.class), entityTypeName(), entityIdTypeName());
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
}
