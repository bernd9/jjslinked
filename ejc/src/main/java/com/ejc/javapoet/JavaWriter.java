package com.ejc.javapoet;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class JavaWriter {

    private final String simpleName;
    private final Optional<String> packageName;
    private final Optional<Class<?>> superClass;
    protected final ProcessingEnvironment processingEnvironment;
    private final Collection<Class<?>> superInterfaces;

    protected Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    protected Types getTypeUtils() {
        return processingEnvironment.getTypeUtils();
    }

    protected TypeElement getTypeElement(String name) {
        return getElementUtils().getTypeElement(name);
    }

    public void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(simpleName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor());
        superClass.ifPresent(builder::superclass);
        superInterfaces.stream().map(TypeName::get).forEach(builder::addSuperinterface);
        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(packageName.orElse(""), typeSpec).build();
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

    protected void writeTypeBody(TypeSpec.Builder builder) {

    }

}
