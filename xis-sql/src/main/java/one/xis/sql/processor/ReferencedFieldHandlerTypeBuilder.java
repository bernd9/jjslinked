package one.xis.sql.processor;

import com.ejc.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.ReferredFieldHandler;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Collection;

@RequiredArgsConstructor
class ReferencedFieldHandlerTypeBuilder {
    private final ReferencedFieldModel model;

    TypeSpec fieldHandlerDeclaration() {
        return TypeSpec.classBuilder(model.getFieldHandlerName())
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .superclass(fieldHandlerSuperClass())
                .addMethod(constructor())
                .addMethod(implementGetFieldValue())
                .addMethod(implementUnlinkFieldValues())
                .addMethod(implementSetFk())
                .build();
    }

    private TypeName fieldHandlerSuperClass() {
        return ParameterizedTypeName.get(ClassName.get(ReferredFieldHandler.class),
                TypeName.get(model.getEntityModel().getIdField().getFieldType()),
                TypeName.get(model.getFieldEntityModel().getType().asType()),
                TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()),
                EntityProxyModel.getEntityProxyTypeName(model.getFieldEntityModel()));
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addStatement("super(new $T(), \"$L\")", EntityCrudHandlerModel.getCrudHandlerTypeName(model.getFieldEntityModel()), model.getReferringField().getColumnName())
                .build();
    }

    private MethodSpec implementGetFieldValue() {
        return MethodSpec.methodBuilder("getFieldValuePk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(model.getFieldEntityModel().getType().asType()), "fieldValue")
                .returns(TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()))
                .addStatement(getGetFieldValuePkStatement())
                .build();
    }

    private CodeBlock getGetFieldValuePkStatement() {
        return model.getFieldEntityModel().getIdField().getGetter()
                .map(this::getGetFieldValuePkStatementByGetter)
                .orElseGet(this::getGetFieldValuePkFieldAccess);
    }

    private CodeBlock getGetFieldValuePkStatementByGetter(ExecutableElement getter) {
        return CodeBlock.builder()
                .add("return fieldValue.$L()", getter.getSimpleName())
                .build();
    }

    private CodeBlock getGetFieldValuePkFieldAccess() {
        EntityModel fieldEntityModel = model.getFieldEntityModel();
        TypeName pkType = TypeName.get(fieldEntityModel.getIdField().getFieldType());
        return CodeBlock.builder()
                .add("return ($T) $T.getFieldValue(fieldValue, \"$L\")", pkType, FieldUtils.class, model.getFieldName())
                .build();
    }

    private MethodSpec implementUnlinkFieldValues() {
        EntityModel fieldEntityModel = model.getFieldEntityModel();
        TypeName pkType = TypeName.get(fieldEntityModel.getIdField().getFieldType());
        String statement = model.isDeleteUnlinked() ? "unlinkByDelete(fieldPks)" : "unlinkBySetFkToNull(fieldPks)";
        return MethodSpec.methodBuilder("unlinkFieldValues")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), pkType), "fieldPks")
                .addStatement(statement)
                .build();
    }

    private MethodSpec implementSetFk() {
        EntityModel fieldEntityModel = model.getFieldEntityModel();
        TypeName pkType = TypeName.get(fieldEntityModel.getIdField().getFieldType());
        return MethodSpec.methodBuilder("setFk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(fieldEntityModel.getType().asType()), "fieldValue")
                .addParameter(pkType, "pk")
                .addStatement(getSetFkStatement())
                .build();
    }

    private CodeBlock getSetFkStatement() {
        return model.getFieldEntityModel().getIdField().getSetter()
                .map(this::getSetFkBySetterStatement)
                .orElseGet(this::getSetFkByFieldAccessStatement);
    }

    private CodeBlock getSetFkBySetterStatement(ExecutableElement element) {
        return CodeBlock.builder()
                .add("$T.setFieldValue(fieldValue, \"$L\", pk)", FieldUtils.class, model.getFieldName())
                .build();
    }

    private CodeBlock getSetFkByFieldAccessStatement() {
        return CodeBlock.builder()
                .add("$T.setFieldValue(fieldValue, \"$L\", pk)", FieldUtils.class, model.getFieldName())
                .build();
    }
}
