package com.ejc.api.context;

import java.util.*;

class MultiValueInjector extends InjectorBase {

    private final ClassReference fieldValueType;

    public MultiValueInjector(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference fieldValueType) {
        super(declaringClass, fieldName, new ClassReference(fieldType));
        this.fieldValueType = fieldValueType;
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        Set<Object> set = factory.getBeans((Class<Object>) fieldValueType.getReferencedClass());
        if (fieldType.isAssignableFrom(Set.class)) {
            return set;
        }
        if (fieldType.isArray()) {
            return set.toArray(new Object[set.size()]);
        }
        if (fieldType.isAssignableFrom(List.class)) {
            return Collections.unmodifiableList(new ArrayList<>(set));
        }
        if (fieldType.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>(set);
        }
        throw new IllegalStateException("unsupported collection type: " + fieldType);

    }
}
