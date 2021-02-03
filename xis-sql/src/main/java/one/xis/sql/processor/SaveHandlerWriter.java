package one.xis.sql.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;
import one.xis.sql.RepositoryImpl;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
public class SaveHandlerWriter {
    private final SaveHandlerModel saveHandlerModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(saveHandlerModel.getSimpleName())
                .addModifiers(Modifier.DEFAULT)
                .addMethod(constructor());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(saveHandlerModel.getPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        return MethodSpec.constructorBuilder().build();
    }

    private void writeTypeBody(TypeSpec.Builder builder) {

    }

    private MethodSpec saveMethod() {
        return MethodSpec.methodBuilder("save")
                .build();
    }

}
