package com.ejc.api.context;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.Set;

@Data
public class SimpleDependencyField {
    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private Object fieldValue;
    private Object owner;

    public boolean isSatisfied() {
        return owner != null && fieldValue != null;
    }

    void setOwner(Object owner, Set<SimpleDependencyField> satisfied) {
        this.owner = owner;
        if (isSatisfied()) {
            satisfied.add(this);
            injectFieldValue();
        }
    }

    void setFieldValue(Object value, Set<SimpleDependencyField> satisfied) {
        this.fieldValue = value;
        if (isSatisfied()) {
            satisfied.add(this);
            injectFieldValue();
        }
    }

    private void injectFieldValue() {
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
