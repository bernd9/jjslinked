package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityProxy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


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
                .addStatement("this.entity = entity")
                .build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        addWrappedEntityField(builder);
        addDirtyFlag(builder);
        implementProxyInterfaceMethods(builder);
        overrideNonComplexSetters(builder);
        /*

        addGetters(builder);
        addSetters(builder);
         */
    }

    private void overrideNonComplexSetters(TypeSpec.Builder builder) {
        Collection<SimpleEntityFieldModel> fields = new HashSet<>(entityProxyModel.getEntityModel().getNonComplexFields());
        fields.remove(entityProxyModel.getEntityModel().getIdField());
        NonComplexFields nonComplexFields = new NonComplexFields("entity", fields);
        nonComplexFields.overrideGetters(builder);
        nonComplexFields.overrideSetters(builder);
    }

    private void implementProxyInterfaceMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementSetPkMethod());
        builder.addMethod(implementGetPkMethod());
        builder.addMethod(implementGetEntityMethod());
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

    private void addWrappedEntityField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(entityTypeName(), "entity", Modifier.PRIVATE, Modifier.FINAL).build());
    }

    static String getMethodParameterName(ExecutableElement method, int parameterIndex) {
        List<String> names = method.getEnclosedElements()
                .stream().filter(e -> e.getKind() == ElementKind.PARAMETER)
                .map(VariableElement.class::cast)
                .map(VariableElement::getSimpleName)
                .map(Name::toString)
                .collect(Collectors.toList());
        return names.get(parameterIndex);
    }




    /*
    private void addGetters(TypeSpec.Builder builder) {
        entityProxyModel.getAllFields().stream()
                .filter(field -> field.getGetter().isPresent())
                .forEach(field -> addGetter(field, builder));
    }
    */

    private void addGetter(SimpleEntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel instanceof EntityFieldModel) {
            addEntityGetter((EntityFieldModel) fieldModel, builder);
        } else if (fieldModel instanceof SimpleEntityFieldModel) {
            addSimpleGetter(fieldModel, builder);
        }
    }

    private void addSimpleGetter(SimpleEntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    private void addEntityGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel.isCollection()) {
            addEntityCollectionGetter(fieldModel, builder);
        } else {
            addSingleEntityGetter(fieldModel, builder);
        }
    }

    private void addSingleEntityGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("if (super.$L() == null)")
                .addStatement("")
                .addStatement("")
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    private void addEntityCollectionGetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement getter = fieldModel.getGetter().orElseThrow();
        builder.addMethod(MethodSpec.methodBuilder(getter.getSimpleName().toString())
                .addStatement("return entity.$L()", getter.getSimpleName())
                .build());
    }

    /*
    private void addSetters(TypeSpec.Builder builder) {
        entityProxyModel.getAllFields().stream()
                .filter(field -> field.getSetter().isPresent())
                .forEach(field -> addSetter(field, builder));
    }

     */

    private void addSetter(SimpleEntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel instanceof EntityFieldModel) {
            addEntitySetter((EntityFieldModel) fieldModel, builder);
        } else {
            addSimpleSetter(fieldModel, builder);
        }
    }

    private void addEntitySetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        if (fieldModel.isCollection()) {
            addEntityCollectionSetter(fieldModel, builder);
        } else {
            addSingleEntitySetter(fieldModel, builder);
        }
    }

    private void addSimpleSetter(SimpleEntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("entity.$L($T $l)", setter.getSimpleName(), parameterTypeName(setter, 0), "value")
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
    }

    private void addSingleEntitySetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("value = new $T<>(value)", entityCollections.getCollectionWrapperType(fieldModel.getFieldType()))
                .addStatement("entity.$L(value)", setter.getSimpleName())
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
    }

    // TODO validate collection must have a generic type
    private void addEntityCollectionSetter(EntityFieldModel fieldModel, TypeSpec.Builder builder) {
        ExecutableElement setter = fieldModel.getSetter().orElseThrow();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(parameterTypeName(setter, 0), "value").build())
                .addStatement("value = new $T<>(value)", entityCollections.getCollectionWrapperType(fieldModel.getFieldType()))
                .addStatement("entity.$L(value)", setter.getSimpleName())
                .addStatement("edited = true");
        builder.addMethod(methodBuilder.build());
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
class NonComplexFields {
    private final String entityFieldName;
    private final Collection<SimpleEntityFieldModel> fields;

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

    private MethodSpec overrideSetter(ExecutableElement setter) {
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

    private MethodSpec overrideGetter(ExecutableElement getter) {
        return MethodSpec.overriding(getter)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.$L()", entityFieldName, getter.getSimpleName())
                .build();
    }
}
