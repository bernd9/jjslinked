package one.xis.sql.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;

@RequiredArgsConstructor
public class RepositoryImplWriter {
    private final EntityModel entityModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        /*
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityModel.getHandlerSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(RepositoryImpl.class))
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        */
        //   JavaFile javaFile = JavaFile.builder(entityHandlerModel.getHandlerPackageName(), typeSpec).build();
        // javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return null;
    }

    private void writeTypeBody(TypeSpec.Builder builder) {

    }

}
