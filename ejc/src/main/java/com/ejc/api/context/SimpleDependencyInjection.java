package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class SimpleDependencyInjection {

    private final Map<ClassReference, Set<SimpleDependencyField>> simpleDependenciesByOwner = new HashMap<>();
    private final Set<SimpleDependencyField> allFields = new HashSet<>();
    private final ApplicationContextInitializer initializer;

    void addFields(Collection<SimpleDependencyField> fields) {
        allFields.addAll(fields);
        fields.forEach(this::doMapping);
    }

    private void doMapping(SimpleDependencyField field) {
        simpleDependenciesByOwner.computeIfAbsent(field.getDeclaringType(), type -> new HashSet<>()).add(field);
    }

    void onSingletonCreated(Object singleton) {
        ClassReference reference = ClassReference.getRef(singleton.getClass().getName());

        Collection<SimpleDependencyField> fieldByFieldType = allFields.stream()
                .filter(field -> reference.isOfType(field.getFieldType()))
                .collect(Collectors.toSet());

        Set<SimpleDependencyField> satisfiedFields = new HashSet<>(fieldByFieldType.stream()
                .filter(field -> field.setFieldValue(singleton))
                .collect(Collectors.toSet()));

        Collection<SimpleDependencyField> fieldByOwnerType = simpleDependenciesByOwner.getOrDefault(reference, Collections.emptySet());

        satisfiedFields.addAll(fieldByOwnerType.stream()
                .filter(field -> field.setOwner(singleton))
                .collect(Collectors.toSet()));

        allFields.removeAll(satisfiedFields);
        fieldByOwnerType.removeAll(satisfiedFields);

        if (fieldByOwnerType.isEmpty()) {
            initializer.onDependencyFieldsComplete(singleton);
        }
    }

    public void removeType(ClassReference type) {
        allFields.removeAll(simpleDependenciesByOwner.remove(type));
    }
}
