package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class EntityUtilWriter {
    private final EntityUtilModel entityUtilModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityUtilModel.getEntityUtilSimpleName())
                .addOriginatingElement(entityUtilModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(entityUtilModel.getEntityUtilPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();

        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createConstructor(builder);
        builder.addMethod(implementGetPk());
        builder.addMethod(implementSetPk());
        builder.addMethod(implementGetPks());
        builder.addMethod(new DoCloneMethodSingle().create());
        builder.addMethod(new DoCloneCollection(ClassName.get(HashSet.class), ClassName.get(Set.class)).create());
        builder.addMethod(new DoCloneCollection(ClassName.get(LinkedList.class), ClassName.get(List.class)).create());
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
    class DoCloneCollection {
        private final ClassName concreteCollectionType;
        private final ClassName collectionInterfaceType;

        MethodSpec create() {
            return MethodSpec.methodBuilder("doClone")
                    .addModifiers(Modifier.STATIC)
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
                    .addModifiers(Modifier.STATIC)
                    .addParameter(entityType(), "o")
                    .returns(entityType())
                    .addStatement("$T rv = new $T()", entityType(), entityType())
                    .addCode(copyFieldValues())
                    .addStatement("return rv")
                    .build();
        }

        private CodeBlock copyFieldValues() {
            CodeBlock.Builder builder = CodeBlock.builder();
            entityModel().getAllFieldsInAlphabeticalOrder().forEach(field -> copyFieldValue(field, builder));
            return builder.build();
        }

        private void copyFieldValue(FieldModel fieldModel, CodeBlock.Builder builder) {
            CodeBlock getFieldValue = getFieldValue(fieldModel);
            if(fieldModel instanceof ReferredFieldModel) {
                copyFieldValueReferredField((ReferredFieldModel) fieldModel, builder, getFieldValue);
            } else if (fieldModel instanceof EntityFieldModel) {
                copyFieldValueForeignKeyField((EntityFieldModel) fieldModel, builder, getFieldValue);
            } else {
                copyFieldValueNonComplex(fieldModel, builder, getFieldValue);
            }
        }

        private void copyFieldValueNonComplex(FieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            builder.addStatement(setFieldValue(fieldModel, getFieldValue));
        }

        private void copyFieldValueForeignKeyField(EntityFieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
            CodeBlock createClone = new CloneEntity(fieldModel).create(getFieldValue);
            builder.addStatement(setFieldValue(fieldModel, createClone));
        }

        private void copyFieldValueReferredField(ReferredFieldModel fieldModel, CodeBlock.Builder builder, CodeBlock getFieldValue) {
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
                .addModifiers(Modifier.STATIC)
                .addParameter(entityCollection(), "collection")
                .addStatement("return collection.stream().map($T::getPk)", entityUtilModel.getEntityUtilTypeName())
                .returns(entityPkStream())
                .build();
    }

    private MethodSpec implementGetPkWithGetter(ExecutableElement getter) {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityUtilModel.getEntityModel().getTypeName(), "entity")
                .addStatement("return entity.$L()", getter.getSimpleName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private MethodSpec implementGetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.STATIC)
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
                .addModifiers(Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("entity.$L(pk)", setter.getSimpleName())
                .build();
    }

    private MethodSpec implementSetPkWithFieldAccess() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.STATIC)
                .addParameter(entityType(), "entity")
                .addParameter(pkType(), "pk")
                .addStatement("return $T.setFieldValue(entity, \"$L\", pk)", FieldUtils.class, pkField().getFieldName())
                .returns(TypeName.get(entityUtilModel.getEntityModel().getIdField().getFieldType()))
                .build();
    }

    private TypeName entityCollection() {
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
