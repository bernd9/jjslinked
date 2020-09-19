package com.ejc.api.context;

import com.ejc.util.ClassUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ClassReference {
    private static Map<String, ClassReference> references = new HashMap<>();

    private Class<?> clazz;

    @Getter
    private final String className;

    ClassReference(Class<?> c) {
        this.clazz = c;
        this.className = c.getName();
    }

    public Class<?> getReferencedClass() {
        if (clazz == null) {
            clazz = ClassUtils.classForName(className);
        }
        return clazz;
    }

    public static ClassReference getRef(String className) {
        return references.computeIfAbsent(className, ClassReference::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassReference that = (ClassReference) o;

        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
}
