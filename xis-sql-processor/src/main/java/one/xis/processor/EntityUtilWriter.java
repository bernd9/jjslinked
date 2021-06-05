package one.xis.processor;

import com.ejc.util.FieldUtils;
import com.ejc.util.ObjectUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class EntityUtilWriter {
    private final EntityUtilModel entityUtilModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityUtilModel.getEntityUtilSimpleName())
                .addOriginatingElement(entityUtilModel.getEntityModel().getType())
                .addModifiers(Modifier.PUBLIC);

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(entityUtilModel.getEntityUtilPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        //System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createConstructor(builder);
        builder.addMethod(implementGetPk());
        builder.addMethod(implementSetPk());
        builder.addMethod(implementGetPks());
        builder.addMethod(implementMapByPk());
        builder.addMethod(implementCompareColumnValues());
        builder.addMethod(new DoCloneMethodSingle().create());
        builder.addMethod(new DoCloneCollection(ClassName.get(HashSet.class), ClassName.get(Set.class)).create());
        builder.addMethod(new DoCloneCollection(ClassName.get(LinkedList.class), ClassName.get(List.class)).create());
        addFieldValueGettersAndSetter(builder);
    }


    @RequiredArgsConstructor
    class CloneEntity {
        private final EntityFieldModel fieldModel;

        CodeBlock create(CodeBlock getFieldValue) {
            return CodeBlock.builder()
                    .add("$T.doClone($L)", EntityUtilModel.getEntityUtilTypeName(fieldModel.getFieldEntityModel()), getFieldValue)
                    .build();
        }
    }

    // TODO : this can be removed, if we are using util-getters and -setters.
    @RequiredArgsConstructor
    class GetFieldValue {
        private final FieldModel fieldModel;

        CodeBlock create() {
            return fieldModel.getGetter().map(this::getFieldValueByGetter).orElseGet(() -> getFieldValueByFieldAccess(fieldModel));
        }


        private CodeBlock getFieldValueByGetter(ExecutableElement getter) {
            return CodeBlock.builder()
                    .add("o.$L()", getter.getSimpleName())
                    .build();
        }

        private CodeBlock getFieldValueByFieldAccess(FieldModel fieldModel) {
            return CodeBlock.builder()
                    .add("($T) $T.getFieldValue(o, \"$L\")", fieldModel.getFieldType(), FieldUtils.class, fieldModel.getFieldName())
                    .build();
        }
    }


    @RequiredArgsConstructor
    class SetFieldValue {
        private final FieldModel fieldModel;

        CodeBlock create(CodeBlock getFieldValue) {
            return fieldModel.getSetter().map(setter -> setValueBySetter(setter, getFieldValue))
                    .orElseGet(() -> setFieldValueByFieldAccess(fieldModel, getFieldValue));
        }

        private CodeBlock setValueBySetter(ExecutableElement setter, CodeBlock getFieldValue) {
            return CodeBlock.builder()
                    .add("rv.$L($L)", setter.getSimpleName(), getFieldValue)
                    .build();
        }

        private CodeBlock setFieldValueByFieldAccess(FieldModel fieldModel, CodeBlock getFieldValue) {
            return CodeBlock.builder()
                    .add("$T.setFieldValue(rv, \"$L\", $L)", FieldUtils.class, fieldModel.getFieldName(), getFieldValue)
                    .build();
        }

    }

    @RequiredArgsConstructor
    class CompareColumnValues {
        private final EntityUtilModel model;

        MethodSpec createCompareMethod() {
            return MethodSpec.methodBuilder("compareColumnValues")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(model.getTypeName(), "entity1")
                    .addParameter(model.getTypeName(), "entity2")
                    .addCode(getCompareFieldsCode())
                    .addStatement("return true")
                    .returns(TypeName.BOOLEAN)
                    .build();
        }

        private CodeBlock getCompareFieldsCode() {
            CodeBlock.Builder builder = CodeBlock.builder();
            model.getAllFields().stream()
                    .sorted(Comparator.comparing(FieldModel::getColumnName))
                    .map(this::compareField)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(builder::add);
            return builder.build();
        }

        private Optional<CodeBlock> compareField(FieldModel fieldModel) {
            if (fieldModel instanceof ForeignKeyFieldModel) {
                return Optional.of(compareForeignKeyField((ForeignKeyFieldModel) fieldModel));
            }
            if (fieldModel instanceof JsonFieldModel) {
                return Optional.of(compareJsonField((JsonFieldModel) fieldModel));
            }
            if (fieldModel instanceof CollectionTableFieldModel) {
                return Optional.of(compareCollectionTableFieldModel((CollectionTableFieldModel) fieldModel));
            }
            if (fieldModel instanceof SimpleEntityFieldModel) {
                return Optional.of(compareNonComplexField((SimpleEntityFieldModel) fieldModel));
            }
            return Optional.empty();
        }

        private CodeBlock compareNonComplexField(SimpleEntityFieldModel fieldModel) {
            String getterName = EntityUtilModel.getGetterName(fieldModel.getFieldName().toString());
            return CodeBlock.builder()
                    .beginControlFlow("if (!$T.equals($L(entity1), $L(entity2)))", ObjectUtils.class, getterName, getterName)
                    .addStatement("return false")
                    .endControlFlow()
                    .build();
        }

        private CodeBlock compareForeignKeyField(ForeignKeyFieldModel fieldModel) {
            String getterName = EntityUtilModel.getGetterName(fieldModel.getFieldName().toString());
            TypeName fieldEntityUtilType = EntityUtilModel.getEntityUtilTypeName(fieldModel.getFieldEntityModel());
            return CodeBlock.builder()
                    .beginControlFlow("if (!$T.equals($T.getPk($L(entity1)), $T.getPk($L(entity2))))", ObjectUtils.class, fieldEntityUtilType, getterName, fieldEntityUtilType, getterName)
                    .addStatement("return false")
                    .endControlFlow()
                    .build();
        }

        private CodeBlock compareJsonField(JsonFieldModel fieldModel) {
            String getterName = EntityUtilModel.getGetterName(fieldModel.getFieldName().toString());
            return CodeBlock.builder()
                    .beginControlFlow("if (!$T.equals($L(entity1), $L(entity2))", ObjectUtils.class, getterName, getterName)
                    .addStatement("return false")
                    .endControlFlow()
                    .build();
        }

        private CodeBlock compareCollectionTableFieldModel(CollectionTableFieldModel fieldModel) {
            String getterName = EntityUtilModel.getGetterName(fieldModel.getFieldName().toString());
            return CodeBlock.builder()
                    .beginControlFlow("if (!$T.equals($L(entity1), $L(entity2))", ObjectUtils.class, getterName, getterName)
                    .addStatement("return false")
                    .endControlFlow()
                    .build();
        }


    }

    @RequiredArgsConstructor
    class DoCloneCollection {
        private final ClassName concreteCollectionType;
        private final ClassName collectionInterfaceType;

        MethodSpec create() {
            return MethodSpec.methodBuilder("doClone")
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                    .addParameter(parameterizedCollectionInterfaceType(), "coll")
                    .returns(parameterizedCollectionInterfaceType())
                    .addStatement("$T rv = new $T<>()", parameterizedCollectionInterfaceType(), concreteCollectionType)
                    .addStatement("coll.stream().map($T::doClone).forEach(rv::add)", entityUtilModel.getEntityUtilTypeName())
                    .addStatement("return rv")
                    .build();
        }

        private TypeName parameterizedCollectionInterfaceType() {
            return ParameterizedTypeName.get(collectionInterfaceType, entityType());
        }

    }

    class DoCloneMethodSingle {
        MethodSpec create() {
            return MethodSpec.methodBuilder("doClone")
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                    .addParameter(entityType(), "o")
                    .returns(entityType())
                    .addStatement("$T rv = new $T()", entityType(), entityType())
                    .addCode(copyFieldValues())
                    .addStatement("return rv")
                    .build();
        }

        private List<? extends FieldModel> getAllFieldsInAlphabeticalOrder() {
            List<? extends FieldModel> list = new ArrayList<>(entityModel().getAllFields());
            Collections.sort(list, Comparator.comparing(f -> f.getFieldName().toString()));
            return list;

        }

        private CodeBlock copyFieldValues() {
            CodeBlock.Builder builder = CodeBlock.builder();
            getAllFieldsInAlphabeticalOrder().forEach(field -> copyFieldValue(field, builder));
            return builder.build();
        }

        private void copyFieldValue(FieldModel fieldModel, CodeBlock.Builder builder) {
            CodeBlock getFieldValue = getFieldValue(fieldModel);
            if(fieldModel instanceof ReferencedFieldModel) {
                copyFieldValueReferredField((ReferencedFieldModel) fieldModel, builder, getFieldValue);
            } else if (fieldModel instanceof ForeignKeyFieldModel) {
                copyFieldValueForeignKeyField((ForeignKeyFieldModel) fieldModel, builder, getFieldValue);
            } else if (fieldModel instanceof CrossTableFieldModel) {
                copyFieldValueCrossTableField((CrossTableFieldModel) fieldModel, builder, getFieldValue);
            } else {
                copyFieldValueNonComplex(fieldModel, builder, getFieldValue);
            }

            // TODO add missing types
        }

        private void copyFieldValueNonComplex(FieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            builder.addStatement(setFieldValue(fieldModel, getFieldValue));
        }

        private void copyFieldValueForeignKeyField(ForeignKeyFieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            CodeBlock createClone = new CloneEntity(fieldModel).create(getFieldValue);
            builder.addStatement(setFieldValue(fieldModel, createClone));
        }

        private void copyFieldValueReferredField(ReferencedFieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            CodeBlock createClone = new CloneEntity(fieldModel).create(getFieldValue);
            builder.addStatement(setFieldValue(fieldModel, createClone));
        }

        private void copyFieldValueCrossTableField(CrossTableFieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            CodeBlock createClone = new CloneEntity(fieldModel).create(getFieldValue);
            builder.addStatement(setFieldValue(fieldModel, createClone));
        }

        private CodeBlock getFieldValue(FieldModel fieldModel) {
            return new GetFieldValue(fieldModel).create();
        }

        private CodeBlock setFieldValue(FieldModel fieldModel, CodeBlock getFieldValue) {
            return new SetFieldValue(fieldModel).create(getFieldValue);
        }
    }
    private void createConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());
    }

    private MethodSpec implementGetPk() {
        return entityUtilModel.getEntityModel().getIdField().getGetter()
                .map(this::implementGetPkWithGetter)
                .orElseGet(this::implementGetPkWithFieldAccess);
    }

    private MethodSpec implementGetPks() {
        return MethodSpec.methodBuilder("getPks")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityCollectionTypeName(), "collection")
                .addStatement("return collection.stream().map($T::getPk)", entityUtilModel.getEntityUtilTypeName())
                .returns(entityPkStream())
                .build();
    }

    private MethodSpec implementGetPkWithGetter(ExecutableElement getter) {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityUtilModel.getEntityModel().getTypeName(), "entity")
                .addStatement("return entity.$L()", getter.getSimpleName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private MethodSpec implementGetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityType(), "entity")
                .addStatement("return $T.getFieldValue(entity, \"$L\")", FieldUtils.class, pkField().getFieldName())
                .returns(pkType())
                .build();
    }

    private MethodSpec implementSetPk() {
        return entityUtilModel.getEntityModel().getIdField().getSetter()
                .map(this::implementSetPkWithSetter)
                .orElseGet(this::implementSetPkWithFieldAccess);
    }


    private MethodSpec implementSetPkWithSetter(ExecutableElement setter) {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("entity.$L(pk)", setter.getSimpleName())
                .build();
    }

    private MethodSpec implementSetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("return $T.setFieldValue(entity, \"$L\", pk)", FieldUtils.class, pkField().getFieldName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private MethodSpec implementMapByPk() {
        return MethodSpec.methodBuilder("mapByPk")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityCollectionTypeName(), "entities")
                .addStatement("return entities.stream().collect($T.toMap($T::getPk, $T.identity()))", Collectors.class, entityUtilModel.getEntityUtilTypeName(), Function.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Map.class), pkType(), entityType()))
                .build();
    }

    private MethodSpec implementCompareColumnValues() {
        return new CompareColumnValues(entityUtilModel).createCompareMethod();
    }

    private void addFieldValueGettersAndSetter(TypeSpec.Builder builder) {
        entityUtilModel.getAllFields().stream().sorted(Comparator.comparing(fieldModel -> fieldModel.getFieldName().toString()))
                .filter(field -> !field.getFieldName().toString().equals("pk"))
                .forEach(field -> addGetterAndSetter(field, builder));

    }

    private void addGetterAndSetter(FieldModel field, TypeSpec.Builder builder) {
        builder.addMethod(implementGetFieldValue(field));
        builder.addMethod(implementSetFieldValue(field));
    }


    private MethodSpec implementGetFieldValue(FieldModel fieldModel) {
        return MethodSpec.methodBuilder(EntityUtilModel.getGetterName(fieldModel.getFieldName().toString()))
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityType(), "entity")
                .returns(TypeName.get(fieldModel.getFieldType()))
                .addStatement(getFieldValueAccessorStatement(fieldModel))
                .build();
    }

    private CodeBlock getFieldValueAccessorStatement(FieldModel fieldModel) {
        return fieldModel.getGetter().map(getter -> CodeBlock.builder()
                .add("return entity.$L()", getter.getSimpleName())
                .build())
                .orElse(CodeBlock.builder()
                        .add("return ($T) $T.getFieldValue(entity, \"$L\")", fieldModel.getFieldType(), FieldUtils.class, fieldModel.getFieldName())
                        .build());
    }

    private MethodSpec implementSetFieldValue(FieldModel fieldModel) {
        return MethodSpec.methodBuilder(EntityUtilModel.getSetterName(fieldModel.getFieldName().toString()))
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(entityType(), "entity")
                .addParameter(TypeName.get(fieldModel.getFieldType()), "value")
                .addStatement(getFieldValueSetterStatement(fieldModel))
                .build();
    }


    private CodeBlock getFieldValueSetterStatement(FieldModel fieldModel) {
        return fieldModel.getSetter().map(setter -> CodeBlock.builder()
                .add("entity.$L(value)", setter.getSimpleName())
                .build())
                .orElse(CodeBlock.builder()
                        .add("$T.setFieldValue(entity, \"$L\", value)", FieldUtils.class, fieldModel.getFieldName())
                        .build());
    }


    private TypeName entityCollectionTypeName() {
        return ParameterizedTypeName.get(ClassName.get(Collection.class), entityType());
    }

    private TypeName entityPkStream() {
        return ParameterizedTypeName.get(ClassName.get(Stream.class), pkType());
    }

    private EntityModel entityModel() {
        return entityUtilModel.getEntityModel();
    }

    private TypeName entityType() {
        return entityModel().getTypeName();
    }

    private TypeName pkType() {
        return TypeName.get(pkField().getFieldType());
    }

    private SimpleEntityFieldModel pkField() {
        return entityUtilModel.getEntityModel().getIdField();
    }


}
