package com.ejc.processor;

import com.ejc.Advice;
import com.google.auto.service.AutoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;

import static com.ejc.util.ReflectionUtils.getAnnotationMirror;
import static com.ejc.util.ReflectionUtils.getAnnotationValues;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Advice"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceAnnotationProcessor extends AbstractProcessor {

    @Getter
    @RequiredArgsConstructor
    class OriginalClass {

        private final String qualifiedName;
        private final Map<String, List<Name>> advices = new HashMap<>();

        void putAdvice(String signature, Name adviceName) {
            advices.computeIfAbsent(signature, s -> new ArrayList<>()).add(adviceName);
        }
    }

    @RequiredArgsConstructor
    class AdviceReflection {
        private final TypeElement advice;

        @Getter
        private String declaringClass;

        @Getter
        private String signature;

        @Getter
        private Name adviceName;

        void doReflection() {
            Map<String, String> annotationValues = getAnnotationValues(getAnnotationMirror(advice, Advice.class));
            declaringClass = annotationValues.get("declaringClass").replace(".class", "");
            signature = annotationValues.get("signature");
            adviceName = advice.getQualifiedName();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, OriginalClass> originalClasses = new HashMap<>();
        roundEnv.getElementsAnnotatedWith(Advice.class)
                .stream().map(TypeElement.class::cast)
                .map(AdviceReflection::new)
                .peek(AdviceReflection::doReflection)
                .forEach(reflection -> originalClasses.computeIfAbsent(reflection.getDeclaringClass(), OriginalClass::new).putAdvice(reflection.getSignature(), reflection.getAdviceName()));
        originalClasses.values().forEach(this::writeImplementation);
        return false;
    }

    private void writeImplementation(OriginalClass superClass) {
        ImplementationWriter writer = new ImplementationWriter(superClass.getQualifiedName(), superClass.getAdvices(), processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
