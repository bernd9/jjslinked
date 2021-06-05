package one.xis.processor;

import com.ejc.util.StringUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
class EntityTableAccessorModel {

    @Getter
    private final EntityModel entityModel;

    static TypeName getEntityTableAccessorTypeName(EntityModel entityModel) {
        return ClassName.get(entityModel.getPackageName(), getEntityTableAccessorSimpleName(entityModel));
    }

    String getEntityTableAccessorSimpleName() {
        return getEntityTableAccessorSimpleName(entityModel);
    }

    String getEntityTableAccessorPackageName() {
        return entityModel.getPackageName();
    }

    ClassName getEntityTableAccessorClassName() {
        return ClassName.get(getEntityTableAccessorPackageName(), getEntityTableAccessorSimpleName());
    }

    static String getEntityTableAccessorSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "TableAccessor";
    }

    static MethodModel getGetSingleValueByFieldValueMethod(ForeignKeyFieldModel fieldModel) {
        EntityModel entityModel = fieldModel.getEntityModel();
        EntityModel fieldEntityModel = fieldModel.getFieldEntityModel();
        String entityPart = StringUtils.firstToUpperCase(fieldEntityModel.getSimpleName());
        String idPart = StringUtils.firstToUpperCase(fieldEntityModel.getIdField().getFieldName().toString());
        TypeName keyType = TypeName.get(fieldModel.getEntityModel().getIdField().getFieldType());
        MethodModel methodModel = new MethodModel(String.format("getBy%s%s", entityPart, idPart));
        methodModel.setReturnType(ParameterizedTypeName.get(ClassName.get(Optional.class), entityModel.getTypeName()));
        ParameterModel keyParam = methodModel.addParameter(keyType, "key");
        methodModel.addStatement("return this.getByColumnValue($L, \"$L\")", keyParam.getName(), fieldModel.getColumnName());
        return methodModel;
    }

    static MethodModel getGetCollectionByFieldValueMethod(ForeignKeyFieldModel fieldModel) {
        EntityModel entityModel = fieldModel.getEntityModel();
        EntityModel fieldEntityModel = fieldModel.getFieldEntityModel();
        String entityPart = StringUtils.firstToUpperCase(fieldEntityModel.getSimpleName());
        String idPart = StringUtils.firstToUpperCase(fieldEntityModel.getIdField().getFieldName().toString());
        TypeName keyType = TypeName.get(fieldModel.getEntityModel().getIdField().getFieldType());
        MethodModel methodModel = new MethodModel(String.format("getAllBy%s%s", entityPart, idPart));
        TypeVariable collectionTypeVariable = methodModel.addTypeVariable("C", Collection.class);
        methodModel.setReturnType(collectionTypeVariable.toTypeVariableName());
        ParameterModel keyParam = methodModel.addParameter(keyType, "key");
        ParameterModel collectionTypeParam = methodModel.addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), collectionTypeVariable.toTypeVariableName()), "collectionType");
        methodModel.addStatement("return ($L) getAllByColumnValue($L, \"$L\", $L)", collectionTypeVariable.getName(), keyParam.getName(), fieldModel.getColumnName(), collectionTypeParam.getName());
        return methodModel;
    }


}
