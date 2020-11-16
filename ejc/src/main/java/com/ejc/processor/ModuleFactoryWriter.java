package com.ejc.processor;

import com.ejc.Value;
import com.ejc.api.context.ClassReference;
import com.ejc.api.context.ModuleFactory;
import com.ejc.javapoet.JavaWriter;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;


public class ModuleFactoryWriter extends JavaWriter {

    private ModuleFactoryWriterModel model;

    private final ClassReferences classReferences;
    private final ParameterReferences parameterReferences;

    @Builder
    public ModuleFactoryWriter(ModuleFactoryWriterModel model, String simpleName, Optional<String> packageName, ProcessingEnvironment processingEnvironment) {
        super(simpleName, packageName, Optional.of(ModuleFactory.class), processingEnvironment);
        this.model = model;
        this.classReferences = new ClassReferences(processingEnvironment);
        this.parameterReferences = new ParameterReferences(processingEnvironment, classReferences);
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("super($L)", classReferences.getRefNonPrimitive(model.getApplicationClass()));
        model.getSingletonElements().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
        writeReplacements(model.getClassReplacements(), constructorBuilder);
    }

    private void writeSingleton(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        writeSetConstructor(model, constructorBuilder);
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", classReferences.getRef(model.getSingleton()), method.getSimpleName()));
        model.getBeanMethods().forEach(method -> writeBeanMethod(model.getSingleton(), method, constructorBuilder));
        model.getConfigFields().forEach(field -> writeConfigField(model.getSingleton(), field, constructorBuilder));
        model.getDependencyFields().forEach(field -> writeDependencyField(model.getSingleton(), field, constructorBuilder));
        model.getCollectionDependencyFields().forEach(field -> writeDependencyCollectionField(model.getSingleton(), field, constructorBuilder));
    }

    private void writeReplacements(Map<TypeElement, TypeElement> replacements, MethodSpec.Builder constructorBuilder) {
        replacements.forEach((type, replacement) -> constructorBuilder.addStatement("addClassReplacement($L, $L)", classReferences.getRef(type), classReferences.getRef(replacement)));
    }

    private void writeBeanMethod(TypeElement singleton, ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        if (method.getParameters().isEmpty()) {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L)",
                    classReferences.getRef(singleton), method.getSimpleName(), classReferences.getRef(method.getReturnType()));
        } else {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L, $L)",
                    classReferences.getRef(singleton), method.getSimpleName(), classReferences.getRef(method.getReturnType()), parameterTypeList(method));
        }
    }

    private void writeConfigField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigField($L, \"$L\", $L.class, \"$L\", \"$L\", $L)",
                classReferences.getRef(singleton), field.getSimpleName(), field.asType(), getConfigFieldKey(field),
                getConfigFieldDefault(field), getConfigFieldMandatory(field));
    }

    private void writeDependencyField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addDependencyField($L, \"$L\", $L)", classReferences.getRef(singleton), field.getSimpleName(), classReferences.getRef(field.asType()));
    }

    private void writeDependencyCollectionField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addCollectionDependencyField($L, \"$L\", $L)",
                classReferences.getRef(singleton), field.getSimpleName(), parameterReferences.getRef(field));
    }

    private void writeSetConstructor(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        if (model.getConstructor().getParameters().isEmpty()) {
            constructorBuilder.addStatement("addConstructor($L)", classReferences.getRef(model.getSingleton()));
        } else {
            constructorBuilder.addStatement("addConstructor($L, $L)", classReferences.getRef(model.getSingleton()), parameterTypeList(model.getConstructor()));
        }
    }

    private CodeBlock parameterTypeList(ExecutableElement e) {
        CodeBlock.Builder builder = CodeBlock.builder();
        Iterator<? extends VariableElement> iterator = e.getParameters().iterator();
        while (iterator.hasNext()) {
            VariableElement variableElement = iterator.next();
            parameterReferences.addParameterReference(variableElement, builder);
            if (iterator.hasNext()) {
                builder.add(",");
            }
        }
        return builder.build();
    }

    private static String getConfigFieldDefault(VariableElement e) {
        return e.getAnnotation(Value.class).defaultValue();
    }

    private static String getConfigFieldKey(VariableElement e) {
        return e.getAnnotation(Value.class).value();
    }

    private static boolean getConfigFieldMandatory(VariableElement e) {
        return e.getAnnotation(Value.class).mandatory();
    }
}


@RequiredArgsConstructor
class ClassReferences {
    private final ProcessingEnvironment processingEnvironment;


    String getRef(TypeElement e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, JavaModelUtils.getClassName(e))
                .build().toString();
    }

    String getRef(TypeMirror e) {
        if (e.getKind().isPrimitive()) {
            return refPrimitive(e);
        }
        TypeElement typeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(e);
        return getRef(typeElement);
    }

    String getRefNonPrimitive(String e) {
        TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(e);
        return getRef(typeElement);
    }

    private String refPrimitive(TypeMirror mirror) {
        return CodeBlock.builder()
                .add("$T.getRefPrimitive(\"$L\")", ClassReference.class, mirror.toString())
                .build().toString();
    }
}


@RequiredArgsConstructor
class ParameterReferences {
    private final ProcessingEnvironment processingEnvironment;
    private final ClassReferences classReferences;

    void addParameterReference(VariableElement variableElement, CodeBlock.Builder builder) {
        if (JavaModelUtils.hasGenericType(variableElement)) {
            TypeMirror genericType = JavaModelUtils.getGenericType(variableElement);
            builder.add(getCollectionParameterRef(variableElement.asType(), genericType));
        } else if (isPrimitive(variableElement)) {
            builder.add(refPrimitive(variableElement.asType()));
        } else {
            builder.add(getSimpleRef(variableElement.asType()));
        }
    }

    private boolean isPrimitive(VariableElement e) {
        return e.asType().getKind().isPrimitive();
    }

    CodeBlock getRef(VariableElement variableElement) {
        CodeBlock.Builder builder = CodeBlock.builder();
        addParameterReference(variableElement, builder);
        return builder.build();
    }

    private String getSimpleRef(TypeMirror e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, e.toString())
                .build().toString();
    }

    private String refPrimitive(TypeMirror mirror) {
        return CodeBlock.builder()
                .add("$T.getRefPrimitive(\"$L\")", ClassReference.class, mirror.toString())
                .build().toString();
    }

    private String getCollectionParameterRef(TypeMirror collectionType, TypeMirror genericType) {
        TypeElement typeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(collectionType);
        TypeElement genTypeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(genericType);
        return getRef(typeElement, genTypeElement);
    }
    
    private String getRef(TypeElement collectionType, TypeElement genericType) {
        return CodeBlock.builder()
                .add("$T.getRef($L.class, \"$L\")", ClassReference.class, JavaModelUtils.getClassName(collectionType), JavaModelUtils.getClassName(genericType))
                .build().toString();
    }
}

