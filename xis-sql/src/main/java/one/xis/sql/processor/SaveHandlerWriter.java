package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntitySaveHandler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

@RequiredArgsConstructor
public class SaveHandlerWriter {
    private final SaveHandlerModel saveHandlerModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(saveHandlerModel.getSimpleName())
                .superclass(TypeName.get(EntitySaveHandler.class))
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(saveHandlerModel.getPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addTypeVariable(TypeVariableName.get("E", TypeName.get(entityModel().getType().asType())))
                .addTypeVariable(TypeVariableName.get("EID", TypeName.get(entityModel().getIdField().getFieldType())))
                .build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {

    }

    private MethodSpec saveMethod() {
        return MethodSpec.methodBuilder("save")
                .build();
    }


    private TypeMirror getProxyType() {
        return processingEnvironment.getElementUtils()
                .getTypeElement(getProxyQualifiedName())
                .asType();
    }

    private String getProxyQualifiedName() {
        return String.format("$s.%s", entityModel().getPackageName(), entityModel().getProxySimpleName());
    }

    private EntityModel entityModel() {
        return saveHandlerModel.getEntityModel();
    }

}
