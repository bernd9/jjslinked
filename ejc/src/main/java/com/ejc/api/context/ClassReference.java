package com.ejc.api.context;

import com.ejc.util.InstanceUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ClassReference {
    private static Map<String, ClassReference> references = new HashMap<>();

    private Class<?> clazz;
    private final String className;

    ClassReference(Class<?> c) {
        this.clazz = c;
        this.className = c.getName();
    }

    public Class<?> getClazz() {
        if (clazz == null) {
            clazz = InstanceUtils.classForName(className);
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
