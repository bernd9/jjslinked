package com.ejc.processor;

import com.ejc.Advice;
import com.ejc.util.ElementUtils;
import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Advice"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceAnnotationProcessor extends AbstractProcessor {

    static final String ADVICE_PACKAGE = "com.ejc.advices";

    @Getter
    @RequiredArgsConstructor
    class ImplementationSuperclass {

        private final String qualifiedName;
        private final Map<String, List<String>> advices = new HashMap<>();

        void putAdvice(String signature, String adviceName) {
            advices.computeIfAbsent(signature, s -> new ArrayList<>()).add(adviceName);
        }
    }


    @RequiredArgsConstructor
    class ImplementationWriter {
        private final ImplementationSuperclass baseClass;
        private final String implName;
        private final Set<String> overrideSignatures;
        private final ProcessingEnvironment processingEnvironment;

        void write() throws IOException {
            JavaFileObject fileObject = processingEnvironment.getFiler().createSourceFile(implName);
            TypeSpec.classBuilder(implName)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(asTypeMirror(baseClass.getQualifiedName()))
                    .addMethods(getOverrideMethods().map(this::createImplMethod).collect(Collectors.toList()))
                    .build();


        }

        // Object invoke(Object bean, Method method, A annotation, Object[] parameters);
        private MethodSpec createImplMethod(ExecutableElement orig) {
            MethodSpec.Builder builder = MethodSpec.overriding(orig)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("java.util.List<? extends com.ejc.MethodAdvice> advices = new ArrayList<>()");
            baseClass.getAdvices().get(signature(orig)).forEach(advice -> builder.addStatement("advices.add(ApplicationContext.getInstance().get($T.class))"));
            return builder.addStatement("Object bean = this")
                    .addStatement("java.lang.reflect.Method method = getClass().getSuperClass().getDeclaredMethod($L)", parameterList(orig))
                    .addStatement("Object[] args = new Object[]{$L}", methodArgs(orig))
                    .addStatement("method.setAccessible(true)")
                    .addStatement("Object rv = null")
                    .addCode("for (com.ejc.MethodAdvice advice : advices) {")
                    .addStatement("rv = advice.invoke(bean, method, args)")
                    .addCode("}")
                    .addStatement("return ($T) rv", orig.getReturnType())
                    .build();
        }

        private String methodArgs(ExecutableElement method) {
            return method.getParameters().stream()
                    .map(VariableElement::getSimpleName) // TODO Generics ?
                    .collect(Collectors.joining(", "));
        }

        private Stream<ExecutableElement> getOverrideMethods() {
            return asTypeElement(baseClass.getQualifiedName()).getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(ExecutableElement.class::cast)
                    .filter(e -> overrideSignatures.contains(signature(e)));
        }

        // TODO share these methods with GenericMethodAnnotationProcessor:
        private String signature(ExecutableElement method) {
            return new StringBuilder(method.getSimpleName())
                    .append("(")
                    .append(parameterList(method))
                    .append(")")
                    .toString();
        }

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

        /*
        private Stream<ExecutableElement> getConstructors() {
            return baseClass.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                    .map(ExecutableElement.class::cast);
        }
        */

    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ImplementationSuperclass> superClasses = new HashMap<>();
        getAdvicePackage(roundEnv)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .map(TypeElement.class::cast)
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
        String implSimpleName = ElementUtils.getSimpleName(superClass.getQualifiedName()) + "Impl";
        String implQualifiedName = superClass.getQualifiedName() + ".Impl";
        String packageName = ElementUtils.getPackageName(superClass.getQualifiedName());
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(implQualifiedName).openOutputStream()))) {
            //try (PrintWriter out = new PrintWriter(System.out)) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import com.ejc.*;");
            out.println("import java.lang.reflect.*;");
            out.printf("public class %s extends %s {", implSimpleName, superClass.getQualifiedName());
            out.println("}");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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

    private void processMethod(ExecutableElement method) {
        //processMethod(method, method.getAnnotation(annotationClass), adviceAnnotation(method));
    }


    private void processAdvice(TypeElement element) {

    }

    private Optional<PackageElement> getAdvicePackage(RoundEnvironment roundEnvironment) {
        Set<? extends Element> e = roundEnvironment.getElementsAnnotatedWith(Advice.class);
        if (e.size() > 0) {
            return java.util.Optional.of(e.iterator().next()).map(Element::getEnclosingElement).map(javax.lang.model.element.PackageElement.class::cast);
        }
        return Optional.empty();
    }
}
