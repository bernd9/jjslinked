package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.ReferredFieldHandler;
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
                .addMethod(implementGetEntityPk())
                .addMethod(implementGetFieldValuePk())
                .addMethod(implementSetFieldValueFk())
                .addMethod(implementUnlinkFieldValues())
                .build();
    }

    private TypeName fieldHandlerSuperClass() {
        return ParameterizedTypeName.get(ClassName.get(ReferredFieldHandler.class),
                TypeName.get(model.getEntityModel().getType().asType()),
                TypeName.get(model.getEntityModel().getIdField().getFieldType()),
                TypeName.get(model.getFieldEntityModel().getType().asType()),
                TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()));
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addStatement("super(new $T(), \"$L\")", EntityCrudHandlerModel.getCrudHandlerTypeName(model.getFieldEntityModel()), model.getReferringField().getColumnName())
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

    private MethodSpec implementGetFieldValuePk() {
        return MethodSpec.methodBuilder("getFieldValuePk")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeName.get(model.getFieldEntityModel().getType().asType()), "fieldValue")
                .returns(TypeName.get(model.getFieldEntityModel().getIdField().getFieldType()))
                .addStatement("return $T.getPk(fieldValue)", EntityUtilModel.getEntityUtilTypeName(model.getFieldEntityModel()))
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
}
