package com.ejc.api.context;

import com.ejc.util.ClassUtils;
import com.ejc.util.TypeUtils;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class ClassReference {

    private static Map<String, ClassReference> references = new ConcurrentHashMap<>();

    private Class<?> clazz;

    @Getter
    private final String className;

    @Getter
    private final Optional<ClassReference> genericType;

    @Getter
    private final Optional<ClassReference> genericType2;

    ClassReference(Class<?> c) {
        this.clazz = c;
        this.className = c.getName();
        this.genericType = Optional.empty();
        this.genericType2 = Optional.empty();
    }

    ClassReference(String className) {
        this.className = className;
        this.genericType = Optional.empty();
        this.genericType2 = Optional.empty();
    }


    ClassReference(String className, String genericType) {
        this.className = className;
        this.genericType = Optional.of(getRef(genericType));
        this.genericType2 = Optional.empty();
    }

    ClassReference(String className, String genericKeyType, String genericValueType) {
        this.className = className;
        this.genericType = Optional.of(getRef(genericKeyType));
        this.genericType2 = Optional.of(getRef(genericValueType));
    }


    public static void flush() {
        references.clear();
    }

    public Class<?> getReferencedClass() {
        if (clazz == null) {
            try {
                clazz = ClassUtils.classForName(className);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return clazz;
    }

    @UsedInGeneratedCode
    public static ClassReference getRef(String className) {
        return references.computeIfAbsent(className, ClassReference::new);
    }

    @UsedInGeneratedCode
    public static ClassReference getRef(Class<? extends Collection> collectionClass, String className) {
        StringBuilder name = new StringBuilder(collectionClass.getName());
        name.append("<");
        name.append(className);
        name.append(">");
        return references.computeIfAbsent(name.toString(), n -> new ClassReference(collectionClass.getName(), className));
    }

    @UsedInGeneratedCode
    public static ClassReference getRef(Class<? extends Map> mapClass, String keyClassName, String valueClassName) {
        StringBuilder name = new StringBuilder(mapClass.getName());
        name.append("<");
        name.append(keyClassName);
        name.append(",");
        name.append(valueClassName);
        name.append(">");
        return references.computeIfAbsent(name.toString(), n -> new ClassReference(mapClass.getName(), keyClassName, valueClassName));
    }

    @UsedInGeneratedCode
    public static ClassReference getRefPrimitive(String className) {
        return references.computeIfAbsent(className, name -> new ClassReference(TypeUtils.getPrimitiveClass(className).orElseThrow()));
    }

    public boolean isInstance(Object o) {
        // TODO check if this caching is faster than class isInstance
        return getReferencedClass().isInstance(o);
        //return isInstance.computeIfAbsent(o, getReferencedClass()::isInstance);
    }

    public boolean equalClass(Object o) {
        return className.equals(o.getClass().getName());
    }

    public boolean isOfType(ClassReference classReference) {
        return classReference.getReferencedClass().isAssignableFrom(getReferencedClass());
        //return isTypeOf.computeIfAbsent(classReference, ref -> ref.getReferencedClass().isAssignableFrom(getReferencedClass()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (ClassReference) o;

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
