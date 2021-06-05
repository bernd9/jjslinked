package one.xis.processor;

import com.ejc.Advice;
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
 * annotated with @{@link Implementation}.This marks this class to have to replace an
 * singleton (if exists) an will get a singleton itself.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Advice"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AdviceAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Collection<MethodAdviceMapping> adviceTargetDescriptions = adviceTargetDescriptions(roundEnv);
        Collection<ImplementationDescriptor> implementationDescriptorCollection = getImplementationDataCollection(adviceTargetDescriptions);
        implementationDescriptorCollection.forEach(this::writeImplementation);
        return false;
    }

    private void writeImplementation(ImplementationDescriptor implementationDescriptor) {
        ImplementationWriter writer = new ImplementationWriter(implementationDescriptor.getOriginalClassQualifiedName(), implementationDescriptor.getAdviceBySignature(), processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<ImplementationDescriptor> getImplementationDataCollection(Collection<MethodAdviceMapping> methodAdviceMappings) {
        Map<String, ImplementationDescriptor> implData = new HashMap<>();
        methodAdviceMappings.stream()
                .forEach(mapping -> {
                    String qualifiedName = mapping.getDeclaringClass();
                    ImplementationDescriptor implementationDescriptor = implData.computeIfAbsent(qualifiedName, ImplementationDescriptor::new);
                    implementationDescriptor.putAdvice(mapping.getSignature(), mapping.getAdvice(), mapping.getPriority());
                });
        return implData.values();
    }


    private Collection<MethodAdviceMapping> adviceTargetDescriptions(RoundEnvironment roundEnv) {
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
    class ImplementationDescriptor {

        private final String originalClassQualifiedName;
        private final Map<String, TreeSet<Item>> advices = new HashMap<>();

        void putAdvice(String signature, TypeElement adviceType, int advicePriority) {
            advices.computeIfAbsent(signature, s -> new TreeSet<>()).add(new Item(adviceType, advicePriority));
        }

        Map<String, List<TypeElement>> getAdviceBySignature() {
            return advices.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> asTypeElementList(entry.getValue())));
        }

        private List<TypeElement> asTypeElementList(TreeSet<Item> items) {
            return items.stream().map(Item::getAdviceType).collect(Collectors.toList());
        }

        @Getter
        @RequiredArgsConstructor
        private class Item implements Comparable<Item> {
            private final TypeElement adviceType;
            private final Integer priority;

            @Override
            public int compareTo(Item o) {
                return priority.compareTo(o.getPriority());
            }
        }
    }

    @RequiredArgsConstructor
    class AdviceReflection {
        private final TypeElement advice;

        @Getter
        private Set<MethodAdviceMapping> targets = new HashSet<>();

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
            int priority = Optional.ofNullable(annotationValues.get("priority")).map(Integer::parseInt).orElse(0);
            targets.add(new MethodAdviceMapping(declaringClass, signature, advice, priority));
        }
    }

    @Getter
    @RequiredArgsConstructor
    class MethodAdviceMapping {
        private final String declaringClass;
        private final String signature;
        private final TypeElement advice;
        private final int priority;
    }

    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }
}
