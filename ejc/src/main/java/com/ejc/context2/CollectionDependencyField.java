package com.ejc.context2;

import com.ejc.util.FieldUtils;
import com.ejc.util.TypeUtils;
import lombok.Getter;

import java.util.Collection;

@Getter
public class CollectionDependencyField extends SingletonCollection {

    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private Collection<Object> fieldValues;

    public CollectionDependencyField(String name, ClassReference declaringType, ClassReference fieldType) {
        super(fieldType.getGenericType().orElseThrow());
        this.name = name;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldValues = TypeUtils.emptyCollection((Class<? extends Collection>) fieldType.getReferencedClass());
    }

    public void setFieldValue(Object owner) {
        FieldUtils.setFieldValue(owner, name, fieldValues);
    }

    public void onSingletonCreated(Object o) {
        if (getElementType().isInstance(o)) {
            fieldValues.add(o);
        }
    }
}
