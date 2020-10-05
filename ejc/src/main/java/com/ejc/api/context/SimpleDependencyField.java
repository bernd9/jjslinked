package com.ejc.api.context;

import com.ejc.util.FieldUtils;
import lombok.Data;

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

    boolean setOwner(Object owner) {
        this.owner = owner;
        if (isSatisfied()) {
            injectFieldValue();
            return true;
        }
        return false;
    }

    boolean setFieldValue(Object value) {
        this.fieldValue = value;
        if (isSatisfied()) {
            injectFieldValue();
            return true;
        }
        return false;
    }

    private void injectFieldValue() {
        FieldUtils.setFieldValue(owner, name, fieldValue);
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
