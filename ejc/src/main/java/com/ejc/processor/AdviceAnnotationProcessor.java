package com.ejc.processor;

import com.ejc.Advice;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ejc.util.JavaModelUtils.*;

/**
 * Creates an implementation as a subclass or implementation of classes annotated by an annotation
 * demanding an advice (annotated with @{@link com.ejc.AdviceClass}). The result is a singleton,
 * but getting annotated with @{@link Implementation}.This marks this class to have to replace an
 * singleton (if exists) an will get a singleton itself.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Advice"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Collection<AdviceMapping> adviceTargetDescriptions = adviceTargetDescriptions(roundEnv);
        Collection<ImplementationData> originalClasses = originalClasses(adviceTargetDescriptions);
        originalClasses.forEach(this::writeImplementation);
        return false;
    }

    private void writeImplementation(ImplementationData superClass) {
        ImplementationWriter writer = new ImplementationWriter(superClass.getOriginalClassQualifiedName(), superClass.getAdvices(), processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<ImplementationData> originalClasses(Collection<AdviceMapping> adviceMappings) {
        Map<String, ImplementationData> originalClasses = new HashMap<>();
        adviceMappings.stream()
                .forEach(mapping -> {
                    String qualifiedName = mapping.getDeclaringClass();
                    ImplementationData implementationData = originalClasses.computeIfAbsent(qualifiedName, ImplementationData::new);
                    implementationData.putAdvice(mapping.getSignature(), mapping.getAdvice());
                });
        return originalClasses.values();
    }


    private Collection<AdviceMapping> adviceTargetDescriptions(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(Advice.class)
                .stream().map(TypeElement.class::cast)
                .map(AdviceReflection::new)
                .peek(AdviceReflection::doReflection)
                .map(AdviceReflection::getTargets)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Getter
    @RequiredArgsConstructor
    class ImplementationData {

        private final String originalClassQualifiedName;
        private final Map<String, List<TypeElement>> advices = new HashMap<>();

        void putAdvice(String signature, TypeElement adviceName) {
            advices.computeIfAbsent(signature, s -> new ArrayList<>()).add(adviceName);
        }
    }

    @RequiredArgsConstructor
    class AdviceReflection {
        private final TypeElement advice;

        @Getter
        private Set<AdviceMapping> targets = new HashSet<>();

        void doReflection() {
            AnnotationMirror adviceMirror = getAnnotationMirror(advice, Advice.class);
            AnnotationValue targets = getAnnotationValue(adviceMirror, "targets");
            List<AnnotationMirror> adviceTargets = (List<AnnotationMirror>) targets.getValue();
            adviceTargets.forEach(this::doReflection);
        }

        private void doReflection(AnnotationMirror adviceTarget) {
            Map<String, String> annotationValues = getAnnotationValues(adviceTarget);
            String declaringClass = annotationValues.get("declaringClass").replace(".class", "");
            String signature = annotationValues.get("signature");
            targets.add(new AdviceMapping(declaringClass, signature, advice));

        }
    }

    @Getter
    @RequiredArgsConstructor
    class AdviceMapping {
        private final String declaringClass;
        private final String signature;
        private final TypeElement advice;
    }

    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }
}
