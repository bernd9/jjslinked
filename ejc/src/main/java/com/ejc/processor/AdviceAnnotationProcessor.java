package com.ejc.processor;

import com.ejc.Advice;
import com.ejc.util.ElementUtils;
import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Advice"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceAnnotationProcessor extends AbstractProcessor {

    @Getter
    @RequiredArgsConstructor
    class ImplementationSuperclass {

        private final String qualifiedName;
        private final Map<String, List<String>> advices = new HashMap<>();

        void putAdvice(String signature, String adviceName) {
            advices.computeIfAbsent(signature, s -> new ArrayList<>()).add(adviceName);
        }
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ImplementationSuperclass> superClasses = new HashMap<>();
        roundEnv.getElementsAnnotatedWith(Advice.class)
                .stream().map(TypeElement.class::cast)
                .forEach(adviceType -> {
                    Map<String, String> annotationValues = getAdviceAnnotationValues(adviceType);
                    String declaringClass = annotationValues.get("declaringClass").replace(".class", "");
                    String signature = annotationValues.get("signature");
                    Name advice = adviceType.getQualifiedName();
                    superClasses.computeIfAbsent(declaringClass, ImplementationSuperclass::new).putAdvice(signature, advice.toString());
                });
        superClasses.values().forEach(this::writeSubclass);
        return false;
    }

    private void writeSubclass(ImplementationSuperclass superClass) {
        String implQualifiedName = superClass.getQualifiedName() + "Impl";
        ImplementationWriter writer = new ImplementationWriter(superClass, implQualifiedName, processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private AnnotationMirror getAdviceAnnotationMirror(TypeElement adviceClass) {
        return Iterables.getOnlyElement(adviceClass.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(Advice.class.getName()))
                .collect(Collectors.toSet()));
    }

    private Map<String, String> getAdviceAnnotationValues(TypeElement method) {
        return getAdviceAnnotationMirror(method).getElementValues().entrySet().stream()
                .collect(Collectors.toMap(e -> getName(e.getKey()), e -> e.getValue().getValue().toString()));
    }


    private String getName(ExecutableElement e) {
        return e.getSimpleName().toString();
    }


    @RequiredArgsConstructor
    class ImplementationWriter {
        private final @NonNull ImplementationSuperclass baseClass;
        private final @NonNull String implName;
        private final @NonNull ProcessingEnvironment processingEnvironment;

        void write() throws IOException {
            TypeSpec typeSpec = TypeSpec.classBuilder(ElementUtils.getSimpleName(implName))
                    .addAnnotation(Implementation.class)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(asTypeMirror(baseClass.getQualifiedName()))
                    .addMethods(getOverrideMethods().map(this::createImplMethod).collect(Collectors.toList()))
                    .build();

            JavaFile javaFile = JavaFile.builder(ElementUtils.getPackageName(implName), typeSpec).build();
            javaFile.writeTo(processingEnvironment.getFiler());
            javaFile.writeTo(Path.of("testxyz"));
        }

        private MethodSpec createImplMethod(ExecutableElement orig) {
            MethodSpec.Builder builder = MethodSpec.overriding(orig)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("java.util.List<com.ejc.MethodAdvice> advices = new java.util.ArrayList<>()");
            baseClass.getAdvices().get(signature(orig)).forEach(advice -> builder.addStatement("advices.add(com.ejc.ApplicationContext.getInstance().getBean($L.class))", advice));
            return builder.addStatement("Object bean = this")
                    .addStatement("java.lang.reflect.Method method = getClass().getSuperclass().getDeclaredMethod(\"$L\", $L)", orig.getSimpleName(), parameterList(orig))
                    .addStatement("Object[] args = new Object[]{$L}", methodArgs(orig))
                    .addStatement("method.setAccessible(true)")
                    .addStatement("Object rv = null")
                    .addCode("for (com.ejc.MethodAdvice advice : advices) {")
                    .addStatement("rv = advice.invoke(bean, method, args)")
                    .addCode("}")
                    .addStatement("return ($T) rv", orig.getReturnType())
                    .build();
        }

        // TODO Util-Class
        private String methodArgs(ExecutableElement method) {
            return method.getParameters().stream()
                    .map(VariableElement::getSimpleName) // TODO Generics ?
                    .collect(Collectors.joining(", "));
        }


        // TODO Util-Class
        private Stream<ExecutableElement> getOverrideMethods() {
            Set<String> overrideSignatures = baseClass.getAdvices().keySet();
            return asTypeElement(baseClass.getQualifiedName()).getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(ExecutableElement.class::cast)
                    .filter(e -> overrideSignatures.contains(signature(e)));
        }

        // TODO share these methods with GenericMethodAnnotationProcessor:
        private String signature(ExecutableElement method) {
            return new StringBuilder(method.getSimpleName())
                    .append("(")
                    .append(method.getParameters().stream()
                            .map(VariableElement::asType)
                            .map(Object::toString)
                            // TODO Generics ?
                            .collect(Collectors.joining(", ")))
                    .append(")")
                    .toString();
        }

        // TODO Util-Class
        private String parameterList(ExecutableElement method) {
            return method.getParameters().stream()
                    .map(param -> param.asType().toString() + ".class") // TODO Generics ?
                    .collect(Collectors.joining(", "));
        }

        private TypeMirror asTypeMirror(String classname) {
            return asTypeElement(classname).asType();
        }

        private TypeElement asTypeElement(String classname) {
            return processingEnvironment.getElementUtils().getTypeElement(classname);
        }
    }
}
