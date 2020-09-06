package com.ejc.processor;

import com.ejc.util.InstanceUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
class ClassReference {
    private static Map<String, ClassReference> references = new HashMap<>();

    private Class<?> clazz;
    private final String className;

    Class<?> getClazz() {
        if (clazz == null) {
            clazz = InstanceUtils.classForName(className);
        }
        return clazz;
    }

    static ClassReference getRef(String className) {
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
