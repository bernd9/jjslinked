package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@Getter
@RequiredArgsConstructor
class DependencyField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final ClassReference fieldType;
    private Object fieldValue;

    @Getter
    private boolean fulfilled;

    void onSingletonCreated(@NonNull Object singleton) {
        if (fieldType.isInstance(singleton)) { // TODO Lists, Arrays etc.
            this.fieldValue = singleton;
        }
    }

    void setFieldValue(@NonNull Object declaringBean) {
        if (fulfilled) {
            throw new IllegalStateException("multiple candidates for field " + fieldName + " in type " + fieldType.getClassName());
        }
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
