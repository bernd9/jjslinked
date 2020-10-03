package com.ejc.processor;

import com.ejc.Singleton;
import com.ejc.Value;
import com.ejc.api.context.ClassReference;
import com.ejc.api.context.ModuleFactory;
import com.ejc.javapoet.JavaWriter;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.Optional;


public class ModuleWriter extends JavaWriter {

    private ModuleWriterModel model;

    @Builder
    public ModuleWriter(ModuleWriterModel model, String simpleName, Optional<String> packageName, ProcessingEnvironment processingEnvironment) {
        super(simpleName, packageName, Optional.of(ModuleFactory.class), processingEnvironment);
        this.model = model;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        model.getSingletonElements().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
    }

    private void writeSingleton(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        writeSetConstructor(model, constructorBuilder);
        writeReplacement(model, constructorBuilder);
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", ref(model.getSingleton()), method));
        model.getBeanMethods().forEach(method -> writeBeanMethod(model.getSingleton(), method, constructorBuilder));
        model.getConfigFields().forEach(field -> writeConfigField(model.getSingleton(), field, constructorBuilder));
        model.getDependencyFields().forEach(field -> writeDependencyField(model.getSingleton(), field, constructorBuilder));
        model.getCollectionDependencyFields().forEach(field -> writeDependencyCollectionField(model.getSingleton(), field, constructorBuilder));
    }

    private void writeReplacement(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        Optional<String> replace = getReplacementAttribute(model.getSingleton());
        if (replace.isPresent()) {
            constructorBuilder.addStatement("addClassToReplace($L)", ref(replace.get()));
        } else if (model.getImplementation() != null) {
            constructorBuilder.addStatement("addClassToReplace($L)", ref(model.getSingleton()));
        }
    }

    private void writeBeanMethod(TypeElement singleton, ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L, $L)",
                ref(singleton), method, ref(method.getReturnType()), parameterTypeList(method));
    }

    private void writeConfigField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigField($L, \"$L\", $L.class, \"$L\", \"$L\", $L)",
                ref(singleton), field.getSimpleName(), field.asType(), getConfigFieldKey(field),
                getConfigFieldDefault(field), getConfigFieldMandatory(field), getConfigFieldMandatory(field));
    }

    private void writeDependencyField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addDependencyField($L, \"$L\", $L)", ref(singleton), field.getSimpleName(), ref(field.asType()));
    }

    private void writeDependencyCollectionField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addCollectionDependencyField($L, \"$L\", $L)",
                ref(singleton), field.getSimpleName(), parameterRef(field));
    }

    private CodeBlock parameterTypeList(ExecutableElement e) {
        CodeBlock.Builder builder = CodeBlock.builder();
        Iterator<? extends VariableElement> iterator = e.getParameters().iterator();
        while (iterator.hasNext()) {
            VariableElement variableElement = iterator.next();
            addParameter(variableElement, builder);
            if (iterator.hasNext()) {
                builder.add(",");
            }
        }
        return builder.build();
    }

    private CodeBlock parameterRef(VariableElement variableElement) {
        CodeBlock.Builder builder = CodeBlock.builder();
        addParameter(variableElement, builder);
        return builder.build();
    }

    private CodeBlock addParameter(VariableElement variableElement, CodeBlock.Builder builder) {
        if (JavaModelUtils.hasGenericType(variableElement)) {
            TypeMirror genericType = JavaModelUtils.getGenericType(variableElement);
            builder.add(ref(variableElement.asType(), genericType));
        } else {
            builder.add(ref(variableElement.asType()));
        }
        return builder.build();
    }

    private void writeSetConstructor(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        if (model.getConstructor().getParameters().isEmpty()) {
            constructorBuilder.addStatement("addConstructor($L)", ref(model.getSingleton()));
        } else {
            constructorBuilder.addStatement("addConstructor($L, $L)", ref(model.getSingleton()), parameterTypeList(model.getConstructor()));
        }
    }

    private Optional<String> getReplacementAttribute(TypeElement annotatedElement) {
        return JavaModelUtils.getAnnotationMirrorOptional(annotatedElement, Singleton.class)
                .map(annotation -> JavaModelUtils.getAnnotationValue(annotation, "replace"))
                .map(AnnotationValue::getValue)
                .map(Object::toString);
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

    private static String ref(TypeMirror e, TypeMirror genericType) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\", \"$L\")", ClassReference.class, e, genericType)
                .build().toString();
    }

    private static String ref(String e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e)
                .build().toString();
    }

    private static String getConfigFieldDefault(VariableElement e) {
        return e.getAnnotation(Value.class).defaultValue();
    }

    private static String getConfigFieldKey(VariableElement e) {
        return e.getAnnotation(Value.class).key();
    }

    private static boolean getConfigFieldMandatory(VariableElement e) {
        return e.getAnnotation(Value.class).mandatory();
    }
}
