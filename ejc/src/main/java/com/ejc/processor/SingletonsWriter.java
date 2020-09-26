package com.ejc.processor;

import com.ejc.api.context.ClassReference;
import com.ejc.javapoet.JavaWriter;
import com.ejc.processor.model.Singletons;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class SingletonsWriter extends JavaWriter {

    private SingletonWriterModels model;

    @Builder
    public SingletonsWriter(SingletonWriterModels model, String packageName, ProcessingEnvironment processingEnvironment) {
        super("SingletonsImpl", packageName, Optional.of(Singletons.class), processingEnvironment);
        this.model = model;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        model.getSingletonWriterModels().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
    }

    private void writeSingleton(SingletonWriterModels.SingletonWriterModel model, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addSingleton($L)", ref(model.getSingleton()));
        constructorBuilder.addStatement("addConstructor($L)", addConstructorMethodParameters(model));
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", ref(model.getSingleton()), method));
        model.getBeanMethods().forEach(method -> constructorBuilder.addStatement("addBeanMethod($L, \"$L\")", ref(model.getSingleton()), method));
        model.getConfigFields().forEach(field -> constructorBuilder.addStatement("addConfigField($L, \"$L\", $L)", ref(model.getSingleton()), field.getSimpleName(), ref(field.asType())));
        model.getDependencyFields().forEach(field -> constructorBuilder.addStatement("addDependencyField($L, \"$L\", $L)", ref(model.getSingleton()), field.getSimpleName(), ref(field.asType())));
    }

    private CodeBlock addConstructorMethodParameters(SingletonWriterModels.SingletonWriterModel model) {
        List<TypeMirror> parameters = new ArrayList<>();
        parameters.add(model.getSingleton().asType());
        parameters.addAll(model.getConstructor().getParameters().stream()
                .map(VariableElement::asType).collect(Collectors.toList()));
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add(parameters.stream()
                .map(t -> ref(t))
                .collect(Collectors.joining(",")));
        return builder.build();
    }

    private static String ref(TypeElement e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e)
                .build().toString();
    }

    private static String ref(TypeMirror e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e)
                .build().toString();
    }
}
