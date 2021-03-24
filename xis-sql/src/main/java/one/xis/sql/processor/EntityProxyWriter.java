package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityProxy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;


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
                .superclass(entityProxyModel.getEntityModel().getType().asType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(entityProxyModel.getEntityProxyPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }


    private void writeTypeBody(TypeSpec.Builder builder) {
        addConstructor(builder);
        addDirtyField(builder);
        addSupplierField(builder);
        implementGetSuppliers(builder);
        implementProxyInterfaceMethods(builder);
        overrideIdSetter(builder);
        overrideSetters(builder);
        //overrideForeignKeyFieldGettersAndSetters(builder);
        /*

        addGetters(builder);
        addSetters(builder);
         */
    }

    private void implementGetSuppliers(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("suppliers")
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), ParameterizedTypeName.get(ClassName.get(Supplier.class), WildcardTypeName.subtypeOf(Object.class))))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return suppliers()")
                .build());
    }


    private void addConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("suppliers = new $T<>()", HashMap.class)
                .build());
    }

    private void overrideIdSetter(TypeSpec.Builder builder) {
        entityModel().getIdField().getSetter()
                .map(this::overrideIdSetter)
                .ifPresent(builder::addMethod);
    }

    private MethodSpec overrideIdSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addStatement("throw new $T(\"primary key is immutable\")", UnsupportedOperationException.class)
                .build();
    }

    private void overrideSetters(TypeSpec.Builder builder) {
        Collection<FieldModel> fields = new HashSet<>(entityProxyModel.getEntityModel().getAllFields());
        fields.remove(entityProxyModel.getEntityModel().getIdField());
        fields.stream()
                .filter(f -> !(f instanceof ReferencedFieldModel))
                .map(FieldModel::getSetter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::overrideSetter)
                .forEach(builder::addMethod);

    }

    protected MethodSpec overrideSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addStatement("dirty = true")
                .addStatement("super.$L(value)", setter.getSimpleName())
                .build();
    }

    private void overrideForeignKeyFieldGettersAndSetters(TypeSpec.Builder builder) {
        ForeignKeyFieldAccessorOverrider foreignKeyFieldAccessorOverrider = new ForeignKeyFieldAccessorOverrider("entity", entityModel().getForeignKeyFields());
        foreignKeyFieldAccessorOverrider.overrideGetters(builder);
        foreignKeyFieldAccessorOverrider.overrideSetters(builder);
    }

    private void implementProxyInterfaceMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementSetPkMethod());
        builder.addMethod(implementGetPkMethod());
        builder.addMethod(implementIsDirtyMethod());
        builder.addMethod(implementDoSetCleanMethod());
    }

    private MethodSpec implementGetPkMethod() {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .returns(entityIdTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.getPk(this)", EntityUtilModel.getEntityUtilTypeName(entityModel()))
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

    private MethodSpec implementDoSetCleanMethod() {
        return MethodSpec.methodBuilder("doSetClean")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("dirty = false")
                .build();
    }

    private MethodSpec implementSetPkMethod() {
        return MethodSpec.methodBuilder("pk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(entityIdTypeName(), "pk").build())
                .addStatement(" if (pk() != null) throw new $T(\"primary key is immutable\")", UnsupportedOperationException.class)
                .addStatement("$T.setPk(this, pk)", EntityUtilModel.getEntityUtilTypeName(entityModel()))
                .build();
    }

    private void addDirtyField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, "dirty", Modifier.PRIVATE).build());
    }

    private void addSupplierField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
                TypeName.get(String.class), TypeName.get(Supplier.class)), "suppliers", Modifier.PRIVATE).build());
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

}


class ForeignKeyFieldAccessorOverrider extends FieldAccessorOverrider {

    private final Collection<ForeignKeyFieldModel> fields;

    ForeignKeyFieldAccessorOverrider(String entityFieldName, Collection<ForeignKeyFieldModel> fields) {
        super(entityFieldName);
        this.fields = fields;
    }

    void overrideGetters(TypeSpec.Builder builder) {
        fields.stream()
                .map(ForeignKeyFieldModel::getGetter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::overrideGetter)
                .forEach(builder::addMethod);
    }

    void overrideSetters(TypeSpec.Builder builder) {
        fields.stream()
                .filter(field -> field.getSetter().isPresent())
                .map(this::overrideSetter)
                .forEach(builder::addMethod);
    }


    private MethodSpec overrideSetter(ForeignKeyFieldModel field) {
        ExecutableElement setter = field.getSetter().orElseThrow();
        String entityProxySimpleName = EntityProxyModel.getEntityProxySimpleName(field.getFieldEntityModel());
        return MethodSpec.methodBuilder(setter.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(setter.getParameters().get(0).asType()), "value").build())
                .addCode(CodeBlock.builder()
                        .addStatement("dirty = true")
                        .beginControlFlow("if (value == null || value instanceof $T)", EntityProxy.class)
                        .addStatement("$L(value)", setter.getSimpleName())
                        .nextControlFlow("else")
                        .addStatement("$L(new $L())", setter.getSimpleName(), entityProxySimpleName)
                        .endControlFlow()
                        .build())
                .build();
    }

}