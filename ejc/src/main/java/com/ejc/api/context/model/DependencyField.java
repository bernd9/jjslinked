package com.ejc.api.context.model;

import com.ejc.api.context.ApplicationContextInitializer;
import com.ejc.api.context.ClassReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class DependencyField {
    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private ApplicationContextInitializer initializer;
    private Object fieldValue;
    private Object owner;

    public boolean isSatisfied() {
        return false;
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

    }


}
