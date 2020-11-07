package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import com.ejc.util.FieldUtils;
import com.ejc.util.TypeUtils;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

@Getter
public class CollectionDependencyField extends SingletonCollection {

    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private int expectedElementCount;
    private Collection<Object> fieldValues;
    private Object owner;


    public CollectionDependencyField(String name, ClassReference declaringType, ClassReference fieldType) {
        super(fieldType.getGenericType().orElseThrow());
        this.name = name;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldValues = TypeUtils.emptyCollection((Class<? extends Collection>) fieldType.getReferencedClass());
    }

    public void registerSingletonTypes(Set<ClassReference> types) {
        // TODO remove this
        expectedElementCount = (int) types.stream()
                // .filter(type -> type.isOfType(elementType))
                .count();
    }

    boolean isSatisfied() {
        return owner != null && fieldValues.size() >= expectedElementCount;
    }

    void addFieldValue(Object o) {
        fieldValues.add(o);
    }

    void injectFieldValue() {
        FieldUtils.setFieldValue(owner, name, fieldValues);
    }
}
