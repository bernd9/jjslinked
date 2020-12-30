package com.ejc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor9;
import javax.lang.model.util.SimpleTypeVisitor9;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaModelUtils {


    public static boolean isNonComplex(TypeMirror typeMirror) {
        if (typeMirror.getKind().isPrimitive()) {
            return true;
        }
        String name = typeMirror.toString().replaceAll("<.*>$", "");
        Class<?> c;
        try {
            c = ClassUtils.classForName(name);
        } catch (Exception e) {
            return false;
        }
        return TypeUtils.isNonComplex(c);
    }

    public static Class<?> nonComplexAsClass(TypeMirror typeMirror) {
        if (!isNonComplex(typeMirror)) {
            throw new IllegalArgumentException("can not be used for complex types");
        }
        String name = typeMirror.toString().replaceAll("<.*>$", "");
        return ClassUtils.classForName(name);
    }

    public static String getClassName(TypeElement element) {
        List<Element> parts = new ArrayList<>();
        Element e = element;
        while (e != null) {
            parts.add(e);
            e = e.getEnclosingElement();
        }
        Collections.reverse(parts);
        StringBuilder s = new StringBuilder();
        Iterator<Element> elementIterator = parts.iterator();
        while (elementIterator.hasNext()) {
            Element part = elementIterator.next();
            if (part.getKind() == ElementKind.PACKAGE) {
                PackageElement packageElement = (PackageElement) part;
                s.append(packageElement.getQualifiedName());
                if (elementIterator.hasNext()) {
                    s.append(".");
                }
            }
            if (part.getKind() == ElementKind.CLASS || part.getKind() == ElementKind.INTERFACE) {
                s.append(part.getSimpleName());
                if (elementIterator.hasNext()) {
                    s.append("$");
                }
            }
        }
        return s.toString();

    }


    // TODO share these methods with GenericMethodAnnotationProcessor:
    public static String signature(ExecutableElement method) {
        return new StringBuilder(method.getSimpleName())
                .append("(")
                .append(method.getParameters().stream()
                        .map(VariableElement::asType)
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .append(")")
                .toString();
    }

    public static boolean hasGenericType(VariableElement variableElement) {
        if (variableElement.asType().getKind().isPrimitive()) {
            return false; // otherwise NullPointerException
        }
        GenericCollectionTypeVisitor visitor = new GenericCollectionTypeVisitor();
        TypeMirror typeMirror = variableElement.asType().accept(visitor, null).orElse(null);
        return typeMirror != null;
    }

    public static boolean hasGenericMapTypes(VariableElement variableElement) {
        if (variableElement.asType().getKind().isPrimitive()) {
            return false; // otherwise NullPointerException
        }
        GenericMapTypeVisitor visitor = new GenericMapTypeVisitor();
        TypeMirror[] typeMirror = variableElement.asType().accept(visitor, null).orElse(null);
        return typeMirror != null;
    }

    public static String stripGenerics(VariableElement collectionVariable) {
        return collectionVariable.asType().toString().replaceAll("<[^>]+>", "");
    }

    public static TypeMirror getGenericCollectionType(VariableElement collectionVariable) {
        GenericCollectionTypeVisitor visitor = new GenericCollectionTypeVisitor();
        return collectionVariable.asType().accept(visitor, null).orElseThrow(() -> new IllegalStateException(collectionVariable + " must have generic type"));
    }

    public static TypeMirror[] getGenericMapTypes(VariableElement mapVariable) {
        GenericMapTypeVisitor visitor = new GenericMapTypeVisitor();
        return mapVariable.asType().accept(visitor, null).orElseThrow(() -> new IllegalStateException(mapVariable + " must have generic types"));
    }


    public static TypeMirror getIterableType(VariableElement collectionVariable) {
        GenericCollectionTypeVisitor visitor = new GenericCollectionTypeVisitor();
        return collectionVariable.asType().accept(visitor, null).orElseThrow(() -> new IllegalStateException(collectionVariable + " must have generic type"));
    }


    private static class GenericCollectionTypeVisitor extends SimpleTypeVisitor9<Optional<TypeMirror>, Void> {

        @Override
        public Optional<TypeMirror> visitDeclared(DeclaredType t, Void aVoid) {
            if (t.getTypeArguments() != null) {
                return (Optional<TypeMirror>) t.getTypeArguments().stream().findFirst();
            }
            return Optional.empty();
        }

    }


    private static class GenericMapTypeVisitor extends SimpleTypeVisitor9<Optional<TypeMirror[]>, Void> {

        @Override
        public Optional<TypeMirror[]> visitDeclared(DeclaredType t, Void aVoid) {
            if (t.getTypeArguments() != null && t.getTypeArguments().size() > 1) {
                return Optional.of(t.getTypeArguments().stream().toArray(TypeMirror[]::new));
            }
            return null;
        }

    }

    public static boolean isCollection(VariableElement var) {
        String name = var.asType().toString().replaceAll("<.*>$", "");
        try {
            return Collection.class.isAssignableFrom(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isMap(VariableElement var) {
        String name = var.asType().toString().replaceAll("<.*>$", "");
        try {
            return Map.class.isAssignableFrom(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * For :
     * <pre>
     *     void method1(int x, String str, Integer[] numbers)
     * </pre>
     * <p>
     * return-value would be "x,str,numbers"
     *
     * @param method
     * @return
     */
    public static String parameterNameList(ExecutableElement method) {
        return method.getParameters().stream()
                .map(VariableElement::getSimpleName)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public static AnnotationMirror getAnnotationMirror(Element annotated, Class<? extends Annotation> annotationClass) {
        return CollectionUtils.getOnlyElement(annotated.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(annotationClass.getName()))
                .collect(Collectors.toSet()));
    }

    // TODO findOnly ..
    public static Optional<? extends AnnotationMirror> getAnnotationMirrorOptional(Element annotated, Class<? extends Annotation> annotationClass) {
        return annotated.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(annotationClass.getName()))
                .findFirst();
    }


    public static Map<String, String> getAnnotationValues(AnnotationMirror annotationMirror) {
        Map<String, String> map = new HashMap<>();
        annotationMirror.getElementValues().forEach((executable, value) -> map.put(executable.getSimpleName().toString(), value.getValue().toString()));
        return map;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String name) {
        return annotationMirror.getElementValues().entrySet().stream()
                .filter(e -> e.getKey().getSimpleName().toString().equals(name))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    private static class ValueVisitor<R> extends SimpleAnnotationValueVisitor9<R, Class<?>> {

        @Override
        public R visitArray(List<? extends AnnotationValue> vals, Class<?> aClass) {

            return super.visitArray(vals, aClass);
        }

    }


    public static String getPackageName(Name qualifiedName) {
        return getPackageName(qualifiedName.toString());
    }

    public static String getPackageName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        return qualifiedName.substring(0, index);
    }

    public static String getSimpleName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        if (index == -1) {
            return qualifiedName;
        }
        return qualifiedName.substring(index + 1);
    }

    public static String getSimpleName(TypeElement e) {
        return getSimpleName(e.getQualifiedName());
    }

    public static String getSimpleName(Name qualifiedName) {
        return getSimpleName(qualifiedName.toString().replaceAll("<.*>$", ""));
    }


    public static String getPackageName(TypeElement e) {
        return getPackageName(e.getQualifiedName());
    }

}
