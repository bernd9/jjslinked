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
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT) // TODO remove abstract
                .superclass(ParameterizedTypeName.get(ClassName.get(EntityTableAccessor.class),
                        entityTypeName(), entityIdTypeName(), entityProxyTypeName()))
                .addOriginatingElement(accessorModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(accessorModel.getEntityTableAccessorPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        implementAbstractMethods(builder);
    }

    private void implementAbstractMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementInsertSingleEntityProxy());
        builder.addMethod(implementInsertEntityCollection());
        builder.addMethod(implementToEntityProxy());
        // TODO
    }

    private MethodSpec implementInsertSingleEntityProxy() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityProxyTypeName(), "entityProxy");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API :builder.addStatement("insertWithApiGeneratedKey(entityProxy)");
            break;
            case DBMS : builder.addStatement("insertWithDbmsGeneratedKey(entityProxy)");
                break;
            case NONE : builder.addStatement("insertWithManuallyPlacedKey(entityProxy) ");
                break;
        }
        return builder.build();
    }


    private MethodSpec implementInsertEntityCollection() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(entityProxyCollectionTypeName(), "entityProxies");
        switch (entityModel().getIdField().getAnnotation(Id.class).generationStrategy()) {
            case API : builder.addStatement("insertWithApiGeneratedKeys(entityProxies)");
                break;
            case DBMS : builder.addStatement("insertWithDbmsGeneratedKeys(entityProxies)");
                break;
            case NONE : builder.addStatement("insertWithManuallyPlacedKeys(entityProxies) ");
                break;
        }
        return builder.build();
    }


    private MethodSpec implementToEntityProxy() {
        return MethodSpec.methodBuilder("toEntityProxy")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(entityProxyTypeName())
                .addParameter(entityTypeName(), "entity")
                .addParameter(TypeName.BOOLEAN, "stored")
                .addStatement("return new $L(entity, stored)", accessorModel.getEntityProxySimpleName())
                .build();

    }


    private EntityModel entityModel() {
        return accessorModel.getEntityModel();
    }

    private ParameterizedTypeName entityProxyCollectionTypeName() {
        return ParameterizedTypeName.get(ClassName.get(Collection.class), entityProxyTypeName());
    }

    private TypeName entityProxyTypeName() {
        String type = new StringBuilder()
                .append(entityModel().getPackageName())
                .append(".")
                .append(EntityProxyModel.getEntityProxySimpleName(entityModel()))
                .toString();
        return TypeName.get(processingEnvironment.getElementUtils().getTypeElement(type).asType());
    }

    private TypeVariableName entityTypeVariableName() {
        return TypeVariableName.get("E", TypeName.get(entityModel().getType().asType()));
    }

    private TypeVariableName entityIdTypeVariableName() {
        return TypeVariableName.get("EID", TypeName.get(entityModel().getIdField().getFieldType()));
    }

    private TypeVariableName entityProxyTypeVariableName() {
        return TypeVariableName.get("P", entityProxyTypeName());
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }

    private TypeName entityIdTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }
}
