package com.ejc.api.context;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class DependencyField {
    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private ApplicationContextInitializer initializer;
    private Object fieldValue;
    private Object owner;

    public boolean isSatisfied() {
        return owner != null && fieldValue != null;
    }

    public void onSingletonCreated(Object o) {
        if (fieldType.isInstance(o)) {
            if (fieldValue != null) {
                // TODO Exception
            }
            fieldValue = o;
        }
        if (declaringType.isInstance(o)) {
            if (owner != null) {
                // TODO Exception
            }
            owner = o;
        }
        if (owner != null && fieldValue != null) {
            setFieldValue();
            initializer.onDependencyFieldComplete(owner);
        }
    }

    private void setFieldValue() {
        try {
            Field field = owner.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(owner, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
