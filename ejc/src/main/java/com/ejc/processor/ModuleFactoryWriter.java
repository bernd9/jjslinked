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
import java.util.Objects;
import java.util.Optional;


public class ModuleFactoryWriter extends JavaWriter {

    private ModuleFactoryWriterModel model;

    @Builder
    public ModuleFactoryWriter(ModuleFactoryWriterModel model, String simpleName, Optional<String> packageName, ProcessingEnvironment processingEnvironment) {
        super(simpleName, packageName, Optional.of(ModuleFactory.class), processingEnvironment);
        this.model = model;
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("super($L)", ref(model.getApplicationClass()));
        model.getSingletonElements().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
    }

    private void writeSingleton(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        writeSetConstructor(model, constructorBuilder);
        writeReplacement(model, constructorBuilder);
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", ref(model.getSingleton()), method.getSimpleName()));
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
        if (method.getParameters().isEmpty()) {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L)",
                    ref(singleton), method.getSimpleName(), ref(method.getReturnType()));
        } else {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L, $L)",
                    ref(singleton), method.getSimpleName(), ref(method.getReturnType()), parameterTypeList(method));
        }

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
                .filter(Objects::nonNull)
                .map(AnnotationValue::getValue)
                .map(Object::toString);
    }

    private static String ref(TypeElement e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, JavaModelUtils.getClassName(e))
                .build().toString();
    }

    private String ref(TypeMirror e) {
        TypeElement typeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(e);
        return ref(typeElement);
    }

    private String ref(TypeMirror collectionType, TypeMirror genericType) {
        TypeElement typeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(collectionType);
        TypeElement genTypeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(genericType);
        return ref(typeElement, genTypeElement);
    }

    private String ref(TypeElement collectionType, TypeElement genericType) {
        return CodeBlock.builder()
                .add("$T.getRef($L.class, \"$L\")", ClassReference.class, JavaModelUtils.getClassName(collectionType), JavaModelUtils.getClassName(genericType))
                .build().toString();
    }

    private String ref(String e) {
        TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(e);
        return ref(typeElement);
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
