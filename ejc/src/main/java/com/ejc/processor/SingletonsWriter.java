package com.ejc.processor;

import com.ejc.api.context.ClassReference;
import com.ejc.javapoet.JavaWriter;
import com.ejc.processor.model.Singletons;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;


public class SingletonsWriter extends JavaWriter {

    private SingletonWriterModels model;

    @Builder
    public SingletonsWriter(SingletonWriterModels model, String packageName, ProcessingEnvironment processingEnvironment) {
        super(Singletons.IMPLEMENTATION_SIMPLE_NAME, packageName, Optional.of(Singletons.class), processingEnvironment);
        this.model = model;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        model.getSingletonWriterModels().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
    }

    private void writeSingleton(SingletonWriterModels.SingletonWriterModel model, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addSingleton($L)", ref(model.getSingleton()));
        addConstructorParameters(model, constructorBuilder);
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", ref(model.getSingleton()), method));
        model.getBeanMethods().forEach(method -> constructorBuilder.addStatement("addBeanMethod($L, \"$L\")", ref(model.getSingleton()), method));
        model.getConfigFields().forEach(field -> constructorBuilder.addStatement("addConfigField($L, \"$L\", $L)", ref(model.getSingleton()), field.getSimpleName(), ref(field.asType())));
        model.getDependencyFields().forEach(field -> constructorBuilder.addStatement("addDependencyField($L, \"$L\", $L)", ref(model.getSingleton()), field.getSimpleName(), ref(field.asType())));
        model.getCollectionDependencyFields().forEach(field -> constructorBuilder.addStatement("addCollectionDependencyField($L, \"$L\", $L, $L)",
                ref(model.getSingleton()), field.getSimpleName(), ref(field.asType()), ref(JavaModelUtils.getGenericType(field))));
    }

    private void addConstructorParameters(SingletonWriterModels.SingletonWriterModel model, MethodSpec.Builder constructorBuilder) {
        model.getConstructorParameters().forEach(parameter -> {
            if (CollectionConstructorParameterElement.class.isInstance(parameter)) {
                CollectionConstructorParameterElement element = (CollectionConstructorParameterElement) parameter;
                constructorBuilder.addStatement("addCollectionConstructorParameter($L, $T.class, $L)", ref(model.getSingleton()), element.getCollectionType(), ref(element.getGenericType()));
            } else if (SimpleConstructorParameterElement.class.isInstance(parameter)) {
                SimpleConstructorParameterElement element = (SimpleConstructorParameterElement) parameter;
                constructorBuilder.addStatement("addConstructorParameter($L, $L)", ref(model.getSingleton()), ref(element.getType()));
            } else throw new IllegalStateException();
        });
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
