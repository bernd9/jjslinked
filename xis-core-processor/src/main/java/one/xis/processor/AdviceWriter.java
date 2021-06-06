package one.xis.processor;

import one.xis.Advice;
import one.xis.AdviceTarget;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import static one.xis.util.JavaModelUtils.signature;

@RequiredArgsConstructor
class AdviceWriter {
    private final Class<? extends Annotation> annotation;
    private final Class<? extends InvocationHandler> advice;
    private final Collection<ExecutableElement> methods;
    private final ProcessingEnvironment processingEnvironment;
    private final int priority;

    void write() throws IOException {
        String simpleName = advice.getSimpleName() + "Mapped";
        //String qualifiedName = PACKAGE + "." + simpleName;

        TypeSpec.Builder builder = TypeSpec.classBuilder(simpleName)
                .addAnnotation(createAdviceAnnotation())
                .addModifiers(Modifier.PUBLIC)
                .superclass(advice);

        methods.stream().map(ExecutableElement::getEnclosingElement).forEach(builder::addOriginatingElement);

        TypeSpec typeSpec = builder.build();
        JavaFile javaFile = JavaFile.builder(advice.getPackageName(), typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private AnnotationSpec createAdviceAnnotation() {
        return AnnotationSpec.builder(Advice.class)
                .addMember("annotation", "$T.class", annotation)
                .addMember("targets", createAdviceTargetAnnotations())
                .addMember("priority", Integer.toString(priority))
                .build();
    }

    private CodeBlock createAdviceTargetAnnotations() {
        CodeBlock.Builder builder = CodeBlock.builder();
        Iterator<ExecutableElement> executableElementIterator = methods.iterator();
        builder.add("{");
        while (executableElementIterator.hasNext()) {
            ExecutableElement executableElement = executableElementIterator.next();
            builder.add("@$T(declaringClass=$T.class, signature=\"$L\")", AdviceTarget.class, executableElement.getEnclosingElement().asType(), signature(executableElement));
            if (executableElementIterator.hasNext()) {
                builder.add(",");
            }
        }
        builder.add("}");
        return builder.build();
    }

    protected String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
