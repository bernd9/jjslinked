package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.GenerationStrategy;
import one.xis.sql.Id;
import one.xis.sql.api.EntityProxy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.*;


class EntityProxyWriter {
    private final EntityProxyModel entityProxyModel;
    private final ProcessingEnvironment processingEnvironment;
    private final EntityCollections entityCollections;

    EntityProxyWriter(EntityProxyModel entityProxyModel, ProcessingEnvironment processingEnvironment) {
        this.entityProxyModel = entityProxyModel;
        this.processingEnvironment = processingEnvironment;
        this.entityCollections = new EntityCollections(processingEnvironment);
    }

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityProxyModel.getEntityProxySimpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityProxy.class),
                        entityTypeName(), entityIdTypeName()))
                .superclass(entityProxyModel.getEntityModel().getType().asType())
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(entityProxyModel.getEntityProxyPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entityTypeName(), "entity").build())
                .addParameter(ParameterSpec.builder(TypeName.BOOLEAN, "stored").build())
                .addStatement("this.entity = entity")
                .addStatement("this.stored = stored")
                .build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addWrappedEntityField(builder);
        addStoredFlag(builder);
        addDirtyFlag(builder);
        implementProxyInterfaceMethods(builder);
        overrideIdGettersAndSetters(builder);
        overrideNonComplexGettersAndSetters(builder);
        /*

        addGetters(builder);
        addSetters(builder);
         */
    }

    private void overrideIdGettersAndSetters(TypeSpec.Builder builder) {
        IdFieldAccessorOverrider idFieldAccessorOverrider = new IdFieldAccessorOverrider("entity", entityModel().getIdField());
        idFieldAccessorOverrider.overrideGetter(builder);
        idFieldAccessorOverrider.overrideSetter(builder);
    }

    private void overrideNonComplexGettersAndSetters(TypeSpec.Builder builder) {
        Collection<SimpleEntityFieldModel> fields = new HashSet<>(entityProxyModel.getEntityModel().getNonComplexFields());
        fields.remove(entityProxyModel.getEntityModel().getIdField());
        NonComplexFieldsAccessorOverrider nonComplexFieldsAccessorOverrider = new NonComplexFieldsAccessorOverrider("entity", fields);
        nonComplexFieldsAccessorOverrider.overrideGetters(builder);
        nonComplexFieldsAccessorOverrider.overrideSetters(builder);
    }

    private void implementProxyInterfaceMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementSetPkMethod());
        builder.addMethod(implementGetPkMethod());
        builder.addMethod(implementGetEntityMethod());
        builder.addMethod(implementStoredMethod());
        builder.addMethod(implementIsDirtyMethod());
        builder.addMethod(implementSetCleanMethod());

    }

    private MethodSpec implementGetPkMethod() {
        return entityModel().getIdField().getGetter().map(this::implementGetPkMethodWithGetter)
                .orElseGet(() -> implementGetPkMethodWithFieldAccess());
    }

    private MethodSpec implementGetPkMethodWithGetter(ExecutableElement getter) {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .returns(entityIdTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build();
    }

    private MethodSpec implementGetPkMethodWithFieldAccess() {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .returns(entityIdTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return ($T) $T.getFieldValue(this, \"$L\")", entityModel().getIdField().getFieldType(), FieldUtils.class, entityModel().getIdField().getFieldName())
                .build();
    }

    private MethodSpec implementGetEntityMethod() {
        return MethodSpec.methodBuilder("entity")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityTypeName())
                .addStatement("return entity")
                .build();
    }


    private MethodSpec implementStoredMethod() {
        return MethodSpec.methodBuilder("stored")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return stored")
                .build();
    }

    private MethodSpec implementIsDirtyMethod() {
        return MethodSpec.methodBuilder("dirty")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return dirty")
                .build();
    }

    private MethodSpec implementSetCleanMethod() {
        return MethodSpec.methodBuilder("clean")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("dirty = false")
                .build();
    }

    private MethodSpec implementSetPkMethod() {
        return entityModel().getIdField().getSetter().map(this::implementSetPkMethodWithSetter)
                .orElse(implementSetPkMethodWithFieldAccess());
    }

    private MethodSpec implementSetPkMethodWithSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entityIdTypeName(), "pk").build())
                .addStatement("entity.$L(pk)", setter.getSimpleName())
                .build();
    }

    private MethodSpec implementSetPkMethodWithFieldAccess() {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entityIdTypeName(), "pk").build())
                .addStatement("$T.setFieldValue(this, \"$L\", pk)", FieldUtils.class, entityModel().getIdField().getFieldName())
                .build();
    }

    private void addDirtyFlag(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, "dirty", Modifier.PRIVATE).build());
    }

    private void addStoredFlag(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, "stored", Modifier.PRIVATE).build());
    }


    private void addWrappedEntityField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(entityTypeName(), "entity", Modifier.PRIVATE, Modifier.FINAL).build());
    }

    private EntityModel entityModel() {
        return entityProxyModel.getEntityModel();
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }


    private TypeName entityIdTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private static TypeName parameterTypeName(ExecutableElement method, int paramIndex) {
        return TypeName.get(method.getParameters().get(paramIndex).asType());
    }

}

@RequiredArgsConstructor
abstract class FieldAccessorOverrider {
    protected final String entityFieldName;

    protected MethodSpec overrideGetter(ExecutableElement getter) {
        return MethodSpec.overriding(getter)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.$L()", entityFieldName, getter.getSimpleName())
                .build();
    }

    protected MethodSpec overrideSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addStatement("dirty = true")
                .addStatement(new CodeBlockBuilder("$L.$L(value)")
                        .withVar(entityFieldName)
                        .withVar(setter.getSimpleName())
                        .build()
                )
                .build();
    }
}


class NonComplexFieldsAccessorOverrider extends FieldAccessorOverrider {

    private final Collection<SimpleEntityFieldModel> fields;

    public NonComplexFieldsAccessorOverrider(String entityFieldName, Collection<SimpleEntityFieldModel> fields) {
        super(entityFieldName);
        this.fields = fields;
    }

    void overrideSetters(TypeSpec.Builder proxyTypeBuilder) {
        fields.stream()
                .map(SimpleEntityFieldModel::getSetter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::overrideSetter)
                .forEach(proxyTypeBuilder::addMethod);
    }

    void overrideGetters(TypeSpec.Builder proxyTypeBuilder) {
        fields.stream()
                .map(SimpleEntityFieldModel::getGetter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::overrideGetter)
                .forEach(proxyTypeBuilder::addMethod);
    }
}

class IdFieldAccessorOverrider extends FieldAccessorOverrider {

    private final SimpleEntityFieldModel idFieldModel;

    IdFieldAccessorOverrider(String entityFieldName, SimpleEntityFieldModel idFieldModel) {
        super(entityFieldName);
        this.idFieldModel = idFieldModel;
    }

    void overrideGetter(TypeSpec.Builder proxyTypeBuilder) {
        idFieldModel.getGetter().map(this::overrideGetter).ifPresent(proxyTypeBuilder::addMethod);
    }

    void overrideSetter(TypeSpec.Builder proxyTypeBuilder) {
        idFieldModel.getSetter().map(this::overrideSetter).ifPresent(proxyTypeBuilder::addMethod);
    }

    protected MethodSpec overrideSetter(ExecutableElement setter) {
          if (idFieldModel.getAnnotation(Id.class).generationStrategy() == GenerationStrategy.NONE){
              return overrideSetterDisabledUpdate(setter);
          }
          return overrideSetterCompletelyBlocked(setter);
    }

    private MethodSpec overrideSetterCompletelyBlocked(ExecutableElement setter) {
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addStatement("throw new $T(\"primary key can not be updated\")", UnsupportedOperationException.class)
                .build();
    }

    private MethodSpec overrideSetterDisabledUpdate(ExecutableElement setter) {
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addStatement("throw new $T(\"changing a primary key is not allowed \")", UnsupportedOperationException.class)
                .build();
    }
}
