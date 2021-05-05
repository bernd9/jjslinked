package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityProxy;
import one.xis.sql.api.FieldValueLoader;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;


class EntityProxyWriter {
    private final EntityProxyModel entityProxyModel;
    private final ProcessingEnvironment processingEnvironment;

    EntityProxyWriter(EntityProxyModel entityProxyModel, ProcessingEnvironment processingEnvironment) {
        this.entityProxyModel = entityProxyModel;
        this.processingEnvironment = processingEnvironment;
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
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        //System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }


    private void writeTypeBody(TypeSpec.Builder typeSpecBuilder) {
        addConstructor1(typeSpecBuilder);
        addConstructor2(typeSpecBuilder);
        addDirtyField(typeSpecBuilder);
        addReadOnlyField(typeSpecBuilder);
        implementProxyInterfaceMethods(typeSpecBuilder);
        overrideIdSetter(typeSpecBuilder);
        overrideSetters(typeSpecBuilder);
        implementLazyLoading(typeSpecBuilder);
    }


    private void implementLazyLoading(TypeSpec.Builder typeSpecBuilder) {
        LazyLoadingFields fields = new LazyLoadingFields(entityProxyModel, processingEnvironment);
        fields.addLoaderFields(typeSpecBuilder);
    }

    private void addConstructor1(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(TypeName.BOOLEAN, "readOnly")
                .addStatement("this.readOnly = readOnly")
                .build());
    }

    private void addConstructor2(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("this(false)")
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
        List<FieldModel> fields = new ArrayList<>(entityProxyModel.getEntityModel().getAllFields());
        fields.remove(entityProxyModel.getEntityModel().getIdField());
        Collections.sort(fields, Comparator.comparing(FieldModel::getColumnName));
        fields.stream()
                .filter(field -> !(field instanceof ReferencedFieldModel))// do not set dirty !
                .filter(field -> !(field instanceof CrossTableFieldModel))// do not set dirty !
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


    private void implementProxyInterfaceMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementGetReadOnlyMethod());
        builder.addMethod(implementSetPkMethod());
        builder.addMethod(implementGetPkMethod());
        builder.addMethod(implementIsDirtyMethod());
        builder.addMethod(implementDoSetCleanMethod());
    }

    private MethodSpec implementGetReadOnlyMethod() {
        return MethodSpec.methodBuilder("readOnly")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return readOnly")
                .build();
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

    private void addReadOnlyField(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(TypeName.BOOLEAN, "readOnly", Modifier.PRIVATE, Modifier.FINAL).build());
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
class LazyLoadingFields {

    private final EntityProxyModel entityProxyModel;
    private final ProcessingEnvironment processingEnvironment;

    void addLoaderFields(TypeSpec.Builder typeSpec) {
        getReferredFieldLoaderFields().forEach(typeSpec::addField);
        getForeignKeyLoaderFields().forEach(typeSpec::addField);
        //getCrossTableLoaderFields().forEach(typeSpec::addField);
    }

    private Stream<FieldSpec> getReferredFieldLoaderFields() {
        return entityProxyModel.getReferredFields().stream().map(this::getReferredFieldLoaderField);
    }

    private FieldSpec getReferredFieldLoaderField(ReferencedFieldModel model) {
        if (model.isCollection()) {
            return getReferredFieldLoaderFieldCollection(model);
        }
        return getReferredFieldLoaderFieldSingleValue(model);
    }

    private FieldSpec getReferredFieldLoaderFieldSingleValue(ReferencedFieldModel model) {
        TypeName fieldEntityTypeName = TypeName.get(model.getFieldType());
        TypeName fieldPkTypeName = TypeName.get(model.getFieldEntityModel().getIdField().getFieldType());
        TypeName entityPkTypeName = TypeName.get(model.getEntityModel().getIdField().getFieldType());
        TypeName fieldLoaderType = ParameterizedTypeName.get(ClassName.get(FieldValueLoader.class), fieldPkTypeName, fieldEntityTypeName);
        TypeName tableAccessor = EntityTableAccessorModel.getEntityTableAccessorTypeName(model.getFieldEntityModel()); // TODO create a field instead of always instantiation
        MethodModel accessorGetter = EntityTableAccessorModel.getGetCollectionByFieldValueMethod(model.getReferringField());
        CodeBlock loaderLambda = CodeBlock.builder().add("key -> new $T().$L(key. $T.class)", tableAccessor, accessorGetter.getName(), entityPkTypeName).build();
        String fieldLoaderFieldName = model.getFieldName() + "Loader";
        return FieldSpec.builder(fieldLoaderType, fieldLoaderFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T($L)", fieldLoaderType, loaderLambda)
                .build();
    }

    private FieldSpec getReferredFieldLoaderFieldCollection(ReferencedFieldModel model) {
        TypeName fieldEntityTypeName = TypeName.get(model.getFieldType());
        TypeName fieldPkTypeName = TypeName.get(model.getFieldEntityModel().getIdField().getFieldType());
        String rawCollectionType = processingEnvironment.getTypeUtils().asElement(model.getFieldType()).toString();
        TypeName fieldLoaderType = ParameterizedTypeName.get(ClassName.get(FieldValueLoader.class), fieldPkTypeName, fieldEntityTypeName);
        TypeName tableAccessor = EntityTableAccessorModel.getEntityTableAccessorTypeName(model.getFieldEntityModel());
        MethodModel accessorGetter = EntityTableAccessorModel.getGetCollectionByFieldValueMethod(model.getReferringField());
        CodeBlock loaderLambda = CodeBlock.builder().add("key -> $T.getInstance().$L(key, $L.class)", tableAccessor, accessorGetter.getName(), rawCollectionType).build();
        String fieldLoaderFieldName = model.getFieldName() + "Loader";
        return FieldSpec.builder(fieldLoaderType, fieldLoaderFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T($L)", fieldLoaderType, loaderLambda)
                .build();
    }

    private Stream<FieldSpec> getForeignKeyLoaderFields() {
        return entityProxyModel.getForeignKeyFields().stream().map(this::getForeignKeyFieldLoaderField);
    }

    // TODO generic loader field
    private FieldSpec getForeignKeyFieldLoaderField(ForeignKeyFieldModel model) {
        TypeName fieldEntityTypeName = TypeName.get(model.getFieldType());
        TypeName fieldPkTypeName = TypeName.get(model.getFieldEntityModel().getIdField().getFieldType());
        TypeName fieldLoaderType = ParameterizedTypeName.get(ClassName.get(FieldValueLoader.class), fieldPkTypeName, fieldEntityTypeName);
        TypeName tableAccessor = EntityTableAccessorModel.getEntityTableAccessorTypeName(model.getFieldEntityModel());
        CodeBlock loaderLambda = CodeBlock.builder().add("key -> $T.getInstance().findById(key).orElse(null)", tableAccessor).build();
        String fieldLoaderFieldName = model.getFieldName() + "Loader";
        return FieldSpec.builder(fieldLoaderType, fieldLoaderFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T($L)", fieldLoaderType, loaderLambda)
                .build();
    }

    private Stream<FieldSpec> getCrossTableLoaderFields() {

        return null;
    }


}