package one.xis.processor;

import lombok.experimental.Delegate;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class QueryResult {
    @Delegate
    private Map<String, Collection<Element>> elements = new HashMap<>();

    <T extends Element> Set<T> getElements(String annotationClass, Class<T> elementType) {
        return elements.getOrDefault(annotationClass, Collections.emptySet()).stream()
                .filter(elementType::isInstance)
                .map(elementType::cast)
                .collect(Collectors.toSet());
    }

    <T extends Element> Set<T> getElements(Class<? extends Annotation> annotationClass, Class<T> elementType) {
        return getElements(annotationClass.getName(), elementType);
    }

}
