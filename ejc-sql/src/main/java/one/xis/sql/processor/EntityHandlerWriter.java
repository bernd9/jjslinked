package one.xis.sql.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityHandler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
public class EntityHandlerWriter {
    private final EntityHandlerModel entityHandlerModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityHandlerModel.getHandlerSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(EntityHandler.class))
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(entityHandlerModel.getHandlerPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return null;
    }

    private void writeTypeBody(TypeSpec.Builder builder) {

    }

}
