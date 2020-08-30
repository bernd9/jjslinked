package com.ejc.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.Set;

@Builder
public class ApplicationContextFactoryWriter {

    private Set<ExecutableElement> initMethods;
    private Set<VariableElement> singleValueDependencies;
    private Set<VariableElement> multiValueDependencies;
    private Set<TypeElement> singletons;
    private String packageName;
    private ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME)
                //.addAnnotation(createSingeltonAnnotation())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ApplicationContextFactoryBase.class);
        //.addMethods(createImplMethods());
        //
        // advices.values().stream().flatMap(List::stream).forEach(builder::addOriginatingElement);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }
}
