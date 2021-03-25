package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityFunctions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
public class EntityFunctionsWriter {

    private final EntityFunctionsModel model;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(model.getEntityFunctionsSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityFunctions.class), model.getTypeName(), model.getPkTypeName()));

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(model.getEntityFunctionsPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        //System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        builder.addMethod(implementCompareColumnValuesMethod());
        builder.addMethod(implementGetPkMethod());
        builder.addMethod(implementSetPkMethod());
    }

    private MethodSpec implementCompareColumnValuesMethod() {
        return MethodSpec.methodBuilder("compareColumnValues")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model.getTypeName(), "entity1")
                .addParameter(model.getTypeName(), "entity2")
                .addStatement("return $T.compareColumnValues(entity1, entity2)", model.getEntityUtilTypeName())
                .returns(TypeName.BOOLEAN)
                .build();
    }

    private MethodSpec implementGetPkMethod() {
        return MethodSpec.methodBuilder("getPk")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model.getTypeName(), "entity")
                .addStatement("return $T.getPk(entity)", model.getEntityUtilTypeName())
                .returns(model.getPkTypeName())
                .build();
    }

    private MethodSpec implementSetPkMethod() {
        return MethodSpec.methodBuilder("setPk")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model.getTypeName(), "entity")
                .addParameter(TypeName.get(model.getEntityModel().getIdField().getFieldType()), "pk")
                .addStatement("$T.setPk(entity, pk)", model.getEntityUtilTypeName())
                .build();
    }
}
