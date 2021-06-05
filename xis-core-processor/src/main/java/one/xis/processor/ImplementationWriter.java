package one.xis.processor;

import com.ejc.JoinPoint;
import com.ejc.MethodAdvice;
import com.ejc.api.context.ApplicationContext;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static com.ejc.util.JavaModelUtils.*;
import static com.ejc.util.JavaPoetUtils.parameterTypeListBlock;

@RequiredArgsConstructor
class ImplementationWriter {
    private final String superClassQualifiedName;
    private final Map<String, List<TypeElement>> advices;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        String implName = superClassQualifiedName + "Impl";
        TypeSpec.Builder builder = TypeSpec.classBuilder(getSimpleName(implName))
                .addAnnotation(createImplAnnotation())
                .addModifiers(Modifier.PUBLIC)
                .superclass(asTypeMirror(superClassQualifiedName))
                .addField(createJoinPointMapField())
                .addMethods(createImplMethodsAndDelegates());
        advices.values().stream().flatMap(List::stream).forEach(builder::addOriginatingElement);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(getPackageName(implName), typeSpec).build();
        StringBuilder s = new StringBuilder();
        javaFile.writeTo(s);
        System.out.println(s);
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private AnnotationSpec createImplAnnotation() {
        return AnnotationSpec.builder(Implementation.class)
                .addMember("forClassName", String.format("\"%s\"", superClassQualifiedName))
                .build();
    }

    private FieldSpec createJoinPointMapField() {
        return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), TypeName.get(JoinPoint.class)), "joinPointsMap")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .initializer("new $T<>()", HashMap.class)
                .build();
    }

    private Collection<MethodSpec> createImplMethodsAndDelegates() {
        List<MethodSpec> methods = new ArrayList<>();
        getMethodsToOverride().map(this::createImplMethodAndDelegate).flatMap(List::stream).forEach(methods::add);
        return methods;
    }

    private List<MethodSpec> createImplMethodAndDelegate(ExecutableElement executableElement) {
        String delegateName = nextDelegateName();
        return Arrays.asList(createDelegate(executableElement, delegateName), createImplMethod(executableElement, delegateName));
    }

    private String nextDelegateName() {
        return "__delegate_" + UUID.randomUUID().toString().replaceAll("-", "");
    }


    private MethodSpec createDelegate(ExecutableElement executableElement, String methodName) {
        return new ExecutionDelegateBuilder(executableElement, methodName).createMethodDelegate();
    }

    private Stream<ExecutableElement> getMethodsToOverride() {
        Set<String> overrideSignatures = advices.keySet();
        return asTypeElement(superClassQualifiedName).getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(e -> overrideSignatures.contains(signature(e)));
    }

    private MethodSpec createImplMethod(ExecutableElement orig, String delegateName) {
        MethodSpec.Builder builder = MethodSpec.overriding(orig)
                .addModifiers(Modifier.PUBLIC);
        if (orig.getReturnType().getKind() == TypeKind.VOID) {
            builder.addStatement("$L(new Object[]{$L})", delegateName, JavaModelUtils.parameterNameList(orig));
        } else {
            builder.addStatement("return $L(new Object[]{$L})", delegateName, JavaModelUtils.parameterNameList(orig));
        }
        return builder.build();
    }

    @RequiredArgsConstructor
    class ExecutionDelegateBuilder {
        private final ExecutableElement executableElement;
        private final String methodName;

        MethodSpec createMethodDelegate() {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(TypeName.get(Object[].class), "args")
                    .addCode(createMethodInstanceBlock(executableElement))
                    .addCode(createJoinPointBlock(signature(executableElement)))
                    .addStatement("");
            if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
                builder.returns(TypeName.get(executableElement.getReturnType()));
                builder.addStatement("return ($T) joinPoint.execute(this, args)", executableElement.getReturnType());
            } else {
                builder.addStatement("joinPoint.execute(this, args)");
            }
            return builder.build();
        }

        private CodeBlock createMethodInstanceBlock(ExecutableElement e) {
            CodeBlock.Builder builder = CodeBlock.builder()
                    .addStatement("$T method", Method.class)
                    .beginControlFlow("try");
            if (e.getParameters().isEmpty()) {
                builder.addStatement("method = getClass().getSuperclass().getDeclaredMethod(\"$L\")", e.getSimpleName());
            } else {
                builder.add("method = getClass().getSuperclass().getDeclaredMethod(\"$L\",", e.getSimpleName())
                        .add(parameterTypeListBlock(e))
                        .add(");");
            }
            return builder.nextControlFlow("catch ($T e)", NoSuchMethodException.class)
                    .addStatement("throw new $T(e)", RuntimeException.class)
                    .endControlFlow()
                    .addStatement("method.setAccessible(true)")
                    .build();
        }

        private CodeBlock createJoinPointBlock(String signature) {
            return CodeBlock.builder()
                    .addStatement("String signature = \"$L\"", signature)
                    .add("$T joinPoint = joinPointsMap.computeIfAbsent(signature, $L)", JoinPoint.class, joinPointLambda(signature))
                    .build();
        }

        private CodeBlock joinPointLambda(String signature) {
            CodeBlock.Builder builder = CodeBlock.builder()
                    .add("sig -> ")
                    .add("{")
                    .addStatement("$T<$T> advices = new $T<>()", List.class, MethodAdvice.class, ArrayList.class);
            advices.get(signature).forEach(advice -> builder.addStatement("advices.add($T.getInstance().getBean($L.class))", ApplicationContext.class, advice.asType()));
            return builder
                    .addStatement("return $T.prepare(advices, method)", JoinPoint.class)
                    .add("}")
                    .build();
        }

    }

    private TypeMirror asTypeMirror(String classname) {
        return asTypeElement(classname).asType();
    }

    private TypeElement asTypeElement(String classname) {
        return processingEnvironment.getElementUtils().getTypeElement(classname);
    }

}
