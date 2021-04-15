package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.CrossTableCrudHandlerFieldHandler;

import javax.lang.model.element.Modifier;
import java.util.Collection;

@RequiredArgsConstructor
class CrossTableFieldHandlerTypeBuilder {
    private final CrossTableFieldModel model;

    TypeSpec fieldHandlerDeclaration() {
        return TypeSpec.classBuilder(model.getFieldHandlerName())
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .superclass(fieldHandlerSuperClass())
                .addMethod(constructor())
                .addMethod(implementGetFieldValuePk())
                .addMethod(implementGetFieldValues())
                .addMethod(implementGetEntityPk())
                .build();
    }

    private TypeName fieldHandlerSuperClass() {
        return ParameterizedTypeName.get(ClassName.get(CrossTableCrudHandlerFieldHandler.class),
                TypeName.get(model.getEntityModel().getType().asType()),
                TypeName.get(model.getEntityModel().getIdField().getFieldType()),
                TypeName.get(model.getFieldEntityModel().getType().asType()),
                TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()));
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addStatement("super(new $T(), new $T(), new $T(), $T.class, $T.class)",
                        EntityTableAccessorModel.getEntityTableAccessorTypeName(model.getFieldEntityModel()),
                        CrossTableAccessorModel.getCrossTableAccessorTypeName(model),
                        EntityFunctionsModel.getEntityFunctionsTypeName(model.getFieldEntityModel()),
                        model.getEntityModel().getTypeName(),
                        model.getFieldEntityModel().getTypeName())
                .build();
    }

    private MethodSpec implementGetFieldValuePk() {
        return MethodSpec.methodBuilder("getFieldValuePk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(model.getFieldEntityModel().getType().asType()), "fieldValue")
                .returns(TypeName.get(model.getEntityModel().getIdField().getFieldType()))
                .addStatement("return $T.getPk(fieldValue)", EntityUtilModel.getEntityUtilTypeName(model.getFieldEntityModel()))
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
        return CodeBlock.builder().add("return $T.$L(entity)", EntityUtilModel.getEntityUtilTypeName(model.getEntityModel()), getterName).build();
    }
}
