package com.jjslinked.processor.util;

import com.jjslinked.model.AnnotationModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationUtil {

    public static Set<AnnotationModel> getAnnotations(Element e) {
        return e.getAnnotationMirrors().stream()
                .map(AnnotationMirror::toString)
                .map(AnnotationModel::new).collect(Collectors.toSet());
    }

    public static Set<AnnotationModel> getAnnotations(Parameter e) {
        return Arrays.stream(e.getAnnotations())
                .map(Annotation::annotationType)
                .map(Class::getName)
                .map(AnnotationModel::new).collect(Collectors.toSet());
    }

    public static Optional<String> getAnnotationAttribute(Element annotatedElement, Class<? extends Annotation> annotation, String attributeName) {
        return annotatedElement.getAnnotationMirrors().stream()
                .filter(mirror -> isMatchingAnnotation(mirror, annotation))
                .map(AnnotationMirror::getElementValues)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .filter(e -> isMatchingAttribute(e.getKey(), attributeName))
                .map(Map.Entry::getValue)
                .map(value -> getAnnotationValueContent(value))
                .findFirst();
    }

    private static boolean isMatchingAnnotation(AnnotationMirror mirror, Class<? extends Annotation> annotation) {
        return mirror.getAnnotationType().toString().equals(annotation.getName());
    }

    private static boolean isMatchingAttribute(ExecutableElement e, String attributeName) {
        return e.getSimpleName().toString().equals(attributeName);
    }

    private static String getAnnotationValueContent(AnnotationValue annotation) {
        return annotation.getValue() != null ? annotation.getValue().toString() : null;
    }
}
