package com.ejc.processor;

import com.ejc.Value;
import com.ejc.api.context.ClassReference;
import com.ejc.api.context.ModuleFactory;
import com.ejc.api.context.ParameterReference;
import com.ejc.api.context.ValueAnnotationReference;
import com.ejc.javapoet.JavaWriter;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
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
        this.parameterReferences = new ParameterReferences(processingEnvironment);
    }

    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("super($L)", classReferences.getRefNonPrimitive(model.getApplicationClass()));
        model.getSingletonElements().forEach(singleton -> writeSingleton(singleton, constructorBuilder));
        writeReplacements(model.getClassReplacements(), constructorBuilder);
    }

    private void writeSingleton(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        writeSetConstructor(model, constructorBuilder);
        model.getInitMethods().forEach(method -> constructorBuilder.addStatement("addInitMethod($L, \"$L\")", classReferences.getSimpleRef(model.getSingleton()), method.getSimpleName()));
        model.getBeanMethods().forEach(method -> writeBeanMethod(model.getSingleton(), method, constructorBuilder));
        model.getConfigFields().forEach(field -> writeConfigField(model.getSingleton(), field, constructorBuilder));
        model.getDependencyFields().forEach(field -> writeDependencyField(model.getSingleton(), field, constructorBuilder));
        model.getCollectionDependencyFields().forEach(field -> writeDependencyCollectionField(model.getSingleton(), field, constructorBuilder));
    }

    private void writeReplacements(Map<TypeElement, TypeElement> replacements, MethodSpec.Builder constructorBuilder) {
        replacements.forEach((type, replacement) -> constructorBuilder.addStatement("addClassReplacement($L, $L)", classReferences.getSimpleRef(type), classReferences.getSimpleRef(replacement)));
    }

    private void writeBeanMethod(TypeElement singleton, ExecutableElement method, MethodSpec.Builder constructorBuilder) {
        if (method.getParameters().isEmpty()) {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L)",
                    classReferences.getSimpleRef(singleton), method.getSimpleName(),
                    classReferences.getSimpleRef(method.getReturnType()));
        } else {
            constructorBuilder.addStatement("addBeanMethod($L, \"$L\", $L, $L)",
                    classReferences.getSimpleRef(singleton),
                    method.getSimpleName(),
                    classReferences.getSimpleRef(method.getReturnType()),
                    parameterTypeList(method));
        }
    }

    private void writeConfigField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addConfigField($L, \"$L\", $L.class, \"$L\", \"$L\", $L)",
                classReferences.getSimpleRef(singleton),
                field.getSimpleName(),
                field.asType(),
                getConfigFieldKey(field),
                getConfigFieldDefault(field),
                getConfigFieldMandatory(field));
    }

    private void writeDependencyField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addDependencyField($L, \"$L\", $L)",
                classReferences.getSimpleRef(singleton),
                field.getSimpleName(),
                classReferences.getSimpleRef(field.asType()));
    }

    private void writeDependencyCollectionField(TypeElement singleton, VariableElement field, MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addStatement("addCollectionDependencyField($L, \"$L\", $L)",
                classReferences.getSimpleRef(singleton),
                field.getSimpleName(),
                classReferences.getCollectionFieldRef(field));
    }


    private void writeSetConstructor(SingletonElement model, MethodSpec.Builder constructorBuilder) {
        if (model.getConstructor().getParameters().isEmpty()) {
            constructorBuilder.addStatement("addConstructor($L)", classReferences.getSimpleRef(model.getSingleton()));
        } else {
            constructorBuilder.addStatement("addConstructor($L, $L)",
                    classReferences.getSimpleRef(model.getSingleton()),
                    parameterTypeList(model.getConstructor()));
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

    String getSimpleRef(TypeElement e) {
        return CodeBlock.builder()
                .add("$T.getRef(\"$L\")", ClassReference.class, JavaModelUtils.getClassName(e))
                .build().toString();
    }

    String getSimpleRef(TypeMirror e) {
        TypeElement typeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(e);
        return getSimpleRef(typeElement);
    }

    String getRefNonPrimitive(String e) {
        TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(e);
        return getSimpleRef(typeElement);
    }

    CodeBlock getCollectionFieldRef(VariableElement field) {
        TypeMirror genericType = JavaModelUtils.getGenericType(field);
        TypeElement collectionType = (TypeElement) processingEnvironment.getTypeUtils().asElement(field.asType());
        TypeElement genTypeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(genericType);
        return getCollectionFieldRef(collectionType, genTypeElement);
    }

    private CodeBlock getCollectionFieldRef(TypeElement collectionType, TypeElement genericType) {
        return CodeBlock.builder()
                .add("$T.getRef($L.class, \"$L\")",
                        ClassReference.class,
                        JavaModelUtils.getClassName(collectionType),
                        JavaModelUtils.getClassName(genericType))
                .build();
    }

}


@RequiredArgsConstructor
class ParameterReferences {
    private final ProcessingEnvironment processingEnvironment;

    void addParameterReference(VariableElement variableElement, CodeBlock.Builder builder) {
        Name name = variableElement.getSimpleName();
        if (JavaModelUtils.hasGenericType(variableElement)) {
            TypeMirror genericType = JavaModelUtils.getGenericType(variableElement);
            builder.add(getCollectionParameterRef(variableElement, genericType, name));
        } else if (isPrimitive(variableElement)) {
            builder.add(getPrimitiveParameterRef(variableElement, name));
        } else {
            builder.add(getSimpleRef(variableElement, name));
        }
    }

    private boolean isPrimitive(VariableElement e) {
        return e.asType().getKind().isPrimitive();
    }

    private String getCollectionParameterRef(VariableElement e, TypeMirror genericType, Name name) {
        TypeElement collectionTypeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(e.asType());
        TypeElement genericTypeElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(genericType);
        return CodeBlock.builder()
                .add("$T.getRef($T.getRef($L.class, \"$L\"), \"$L\", $L)",
                        ParameterReference.class,
                        ClassReference.class,
                        JavaModelUtils.getClassName(collectionTypeElement),
                        JavaModelUtils.getClassName(genericTypeElement),
                        name,
                        valueAnnotationReference(e))
                .build().toString();
    }

    private String getPrimitiveParameterRef(VariableElement e, Name name) {
        return CodeBlock.builder()
                .add("$T.getRef($T.getRefPrimitive(\"$L\"), \"$L\", $L)",
                        ParameterReference.class,
                        ClassReference.class,
                        e.asType().toString(),
                        name,
                        valueAnnotationReference(e))
                .build().toString();
    }

    private String getSimpleRef(VariableElement e, Name name) {
        return CodeBlock.builder()
                .add("$T.getRef($T.getRef(\"$L\"), \"$L\", $L)",
                        ParameterReference.class,
                        ClassReference.class,
                        e.asType().toString(),
                        name,
                        valueAnnotationReference(e))
                .build().toString();
    }

    private CodeBlock valueAnnotationReference(VariableElement e) {
        Value valueAnnotation = e.getAnnotation(Value.class);
        if (valueAnnotation == null) {
            return CodeBlock.builder().add("$T.empty()", Optional.class).build();
        }
        return CodeBlock.builder().add("$T.of(new $T(\"$L\",\"$L\",$L))",
                Optional.class,
                ValueAnnotationReference.class,
                valueAnnotation.value(),
                valueAnnotation.defaultValue(),
                valueAnnotation.mandatory())
                .build();

    }


}

