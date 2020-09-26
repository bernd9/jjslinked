package com.ejc.javapoet;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JavaWriter {

    private final String simpleName;
    private final String packageName;
    private final Optional<Class<?>> superClass;
    private final ProcessingEnvironment processingEnvironment;

    public void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(simpleName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor());
        superClass.ifPresent(builder::superclass);
        writeType(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private MethodSpec constructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        writeConstructor(constructorBuilder);
        return constructorBuilder.build();
    }

    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {

    }

    protected void writeType(TypeSpec.Builder builder) {

    }

}
