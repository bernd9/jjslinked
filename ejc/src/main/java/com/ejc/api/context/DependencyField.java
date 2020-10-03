package com.ejc.api.context;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class DependencyField {
    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
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
            ApplicationContextInitializer.getInstance().remove(this);
            ApplicationContextInitializer.getInstance().onDependencyFieldComplete(owner);
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


    @Override
    public boolean equals(Object o) {
        return o.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (declaringType != null ? declaringType.hashCode() : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        return result;
    }
}
