package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.*;

@Getter
class MultiDependencyField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final ClassReference fieldValueType;
    private Collection<Object> fieldValue;

    @Getter
    private boolean fulfilled;

    MultiDependencyField(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference fieldValueType) {
        this.declaringClass = declaringClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldValueType = fieldValueType;
        this.fieldValue = createCollection();
    }

    private Collection<Object> createCollection() {
        if (fieldType.isAssignableFrom(Set.class)) {
            return new HashSet();
        }
        if (fieldType.isAssignableFrom(List.class)) {
            return new ArrayList<>();
        }
        if (fieldType.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>();
        }
        throw new IllegalStateException("unsupported collection type: " + fieldType);
    }

    void onSingletonCreated(@NonNull Object singleton) {
        if (fieldValueType.isInstance(singleton)) {
            this.fieldValue.add(singleton);
        }
    }

    void setFieldValue(@NonNull Object declaringBean) {
        try {
            Field field = getField(fieldName);
            field.setAccessible(true);
            field.set(declaringBean, fieldValue);
            fulfilled = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Field getField(Object bean) throws NoSuchFieldException {
        Class<?> c = bean.getClass();
        while (!c.equals(Object.class)) {
            if (c.equals(declaringClass.getReferencedClass())) {
                try {
                    return c.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            c = c.getSuperclass();
        }
        throw new NoSuchFieldException();
    }
}
