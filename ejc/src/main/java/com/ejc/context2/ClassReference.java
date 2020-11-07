package com.ejc.context2;

import com.ejc.processor.ModuleWriter;
import com.ejc.util.ClassUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ClassReference {

    private static Map<String, ClassReference> references = new HashMap<>();

    private Class<?> clazz;

    @Getter
    private final String className;

    @Getter
    private final Optional<ClassReference> genericType;

    @UsedInGeneratedCode(ModuleWriter.class)
    ClassReference(Class<?> c) {
        this.clazz = c;
        this.className = c.getName();
        this.genericType = Optional.empty();
    }

    ClassReference(String className) {
        this.className = className;
        this.genericType = Optional.empty();
    }


    public static void flush() {
        references.clear();
    }

    ClassReference(String className, String genericType) {
        this.className = className;
        this.genericType = Optional.of(getRef(genericType));
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

    public boolean isInstance(Object o) {
        // TODO check if this caching is faster than class isInstance
        return getReferencedClass().isInstance(o);
        //return isInstance.computeIfAbsent(o, getReferencedClass()::isInstance);
    }

    public boolean isOfType(ClassReference classReference) {
        return classReference.getReferencedClass().isAssignableFrom(getReferencedClass());
        //return isTypeOf.computeIfAbsent(classReference, ref -> ref.getReferencedClass().isAssignableFrom(getReferencedClass()));
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

    @Override
    public String toString() {
        return "ClassReference{" +
                "className='" + className + '\'' +
                '}';
    }

    public boolean matches(Class<?> elementType) {
        return elementType.isAssignableFrom(getReferencedClass());
    }
}
