package com.ejc.api.context;

import com.ejc.util.FieldUtils;
import com.ejc.util.TypeUtils;
import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
public class CollectionDependencyField {

    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private int expectedElementCount;
    private Collection<Object> fieldValues;
    private Object owner;
    private ClassReference elementType;

    public CollectionDependencyField(String name, ClassReference declaringType, ClassReference fieldType) {
        this.name = name;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldValues = TypeUtils.emptyCollection((Class<? extends Collection>) fieldType.getReferencedClass());
        this.elementType = fieldType.getGenericType().orElseThrow();
    }

    public void registerSingletonTypes(Set<ClassReference> types) {
        expectedElementCount = (int) types.stream()
                .filter(type -> type.isOfType(elementType))
                .count();
    }

    public boolean isSatisfied() {
        return owner != null && fieldValues.size() >= expectedElementCount;
    }

    public void onSingletonCreated(Object o) {
        if (elementType.isInstance(o)) {
            fieldValues.add(o);
        }
        if (declaringType.isInstance(o)) {
            if (owner != null) {
                // TODO Exception
            }
            owner = o;
        }
        if (isSatisfied()) {
            FieldUtils.setFieldValue(owner, name, fieldValues);
            ApplicationContextInitializer.getInstance().remove(this);
            ApplicationContextInitializer.getInstance().onDependencyFieldComplete(owner);
        }
    }
}
