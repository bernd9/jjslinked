package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityCrudHandlerSession;
import one.xis.sql.api.ReferredCrudHandlerFieldHandler;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
class ReferencedFieldHandlerTypeBuilder {
    private final ReferencedFieldModel model;

    TypeSpec fieldHandlerDeclaration() {
        return TypeSpec.classBuilder(model.getFieldHandlerName())
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .superclass(fieldHandlerSuperClass())
                .addMethod(constructor())
                .addMethod(implementGetEntityPk())
                .addMethod(implementSetFieldValueFk())
                .addMethod(implementHandleUnlinkedFieldValues())
                .addMethod(implementGetFieldValues())
                .build();
    }

    private TypeName fieldHandlerSuperClass() {
        return ParameterizedTypeName.get(ClassName.get(ReferredCrudHandlerFieldHandler.class),
                TypeName.get(model.getEntityModel().getType().asType()),
                TypeName.get(model.getEntityModel().getIdField().getFieldType()),
                TypeName.get(model.getFieldEntityModel().getType().asType()),
                TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()));
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addStatement("super(new $T(), new $T(), $T.class, $T.class)",
                        EntityTableAccessorModel.getEntityTableAccessorTypeName(model.getFieldEntityModel()),
                        EntityFunctionsModel.getEntityFunctionsTypeName(model.getFieldEntityModel()),
                        model.getEntityModel().getTypeName(),
                        model.getFieldEntityModel().getTypeName())
                .build();
    }

    private MethodSpec implementGetEntityPk() {
        return MethodSpec.methodBuilder("getEntityPk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(model.getEntityModel().getType().asType()), "entity")
                .returns(TypeName.get(model.getEntityModel().getIdField().getFieldType()))
                .addStatement("return $T.getPk(entity)", EntityUtilModel.getEntityUtilTypeName(model.getEntityModel()))
                .build();
    }

    private MethodSpec implementSetFieldValueFk() {
        EntityModel fieldEntityModel = model.getFieldEntityModel();
        TypeName entityType = TypeName.get(model.getEntityModel().getType().asType());
        String setterName = EntityUtilModel.getSetterName(model.getReferringField().getFieldName().toString());
        TypeName entityUtilTypeName = EntityUtilModel.getEntityUtilTypeName(model.getFieldEntityModel());
        return MethodSpec.methodBuilder("setFieldValueFk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(fieldEntityModel.getType().asType()), "fieldValue")
                .addParameter(entityType, "entity")
                .addStatement("$T.$L(fieldValue, entity)", entityUtilTypeName, setterName)
                .build();
    }

    private MethodSpec implementHandleUnlinkedFieldValues() {
        TypeName entityPkType = TypeName.get(model.getFieldEntityModel().getIdField().getFieldType());
        String statement = model.isDeleteUnlinked() ? "unlinkByDelete(fieldValues, crudHandlerSession)" : "unlinkBySetFkToNull(fieldValues, crudHandlerSession)";
        return MethodSpec.methodBuilder("handleUnlinkedFieldValues")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(entityPkType, "entityId")
                .addParameter(fieldCollectionType(), "fieldValues")
                .addParameter(TypeName.get(EntityCrudHandlerSession.class), "crudHandlerSession")
                .addStatement(statement)
                .build();
    }

    private TypeName fieldCollectionType() {
        return ParameterizedTypeName.get(ClassName.get(Collection.class), model.getFieldEntityModel().getTypeName());
    }

    private MethodSpec implementGetFieldValues() {
        return MethodSpec.methodBuilder("getFieldValues")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(model.getEntityModel().getType().asType()), "entity")
                .returns(fieldCollectionType())
                .addStatement(implementFieldValuesStatement())
                .build();
    }

    private CodeBlock implementFieldValuesStatement() {
        String getterName = EntityUtilModel.getGetterName(model.getFieldName().toString());
        if (!JavaModelUtils.isCollection(model.getField())) {
            return CodeBlock.builder().add("return $T.singletonList($T.$L(entity))", Collections.class, EntityUtilModel.getEntityUtilTypeName(model.getEntityModel()), getterName).build();
        }
        return CodeBlock.builder().add("return $T.$L(entity)", EntityUtilModel.getEntityUtilTypeName(model.getEntityModel()), getterName).build();
    }
}
