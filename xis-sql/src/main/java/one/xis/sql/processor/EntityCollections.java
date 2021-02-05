package one.xis.sql.processor;

import one.xis.sql.api.EntityArrayList;
import one.xis.sql.api.EntityHashSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.*;


class EntityCollections {

    private final ProcessingEnvironment processingEnvironment;
    private final TypeMirror collectionTypeMirror;
    private final TypeMirror setTypeMirror;
    private final TypeMirror listTypeMirror;
    private final TypeMirror arrayListTypeMirror;
    private final TypeMirror linkedListTypeMirror;
    private final TypeMirror hashSetTypeMirror;
    private final TypeMirror treeSetTypeMirror;

    EntityCollections(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
        this.collectionTypeMirror = typeMirror(Collection.class);
        this.setTypeMirror = typeMirror(Set.class);
        this.listTypeMirror = typeMirror(List.class);
        this.arrayListTypeMirror = typeMirror(ArrayList.class);
        this.linkedListTypeMirror = typeMirror(LinkedList.class);
        this.hashSetTypeMirror = typeMirror(HashSet.class);
        this.treeSetTypeMirror = typeMirror(TreeSet.class);
    }

    private TypeMirror typeMirror(Class<?> c) {
        return processingEnvironment.getElementUtils().getTypeElement(c.getName()).asType();
    }

    @SuppressWarnings("unchecked")
    Class<? extends Collection<?>> getCollectionWrapperType(TypeMirror collectionClass) {
        // order is important, here !
        if (isAssignable(collectionClass, arrayListTypeMirror)) {
            return (Class<? extends Collection<?>>) EntityArrayList.class;
        }
        if (isAssignable(collectionClass, linkedListTypeMirror)) {
            throw new UnsupportedOperationException(); // TODO
        }
        if (isAssignable(collectionClass, hashSetTypeMirror)) {
            return (Class<? extends Collection<?>>) EntityHashSet.class;
        }
        if (isAssignable(collectionClass, treeSetTypeMirror)) {
            throw new UnsupportedOperationException(); // TODO
        }
        if (isAssignable(collectionClass, setTypeMirror)) {
            return (Class<? extends Collection<?>>) EntityHashSet.class;
        }
        if (isAssignable(collectionClass, listTypeMirror)) {
            return (Class<? extends Collection<?>>) EntityArrayList.class;
        }
        if (isAssignable(collectionClass, collectionTypeMirror)) {
            return (Class<? extends Collection<?>>) EntityHashSet.class;
        }
        return null;
    }

    private boolean isAssignable(TypeMirror candidate, TypeMirror type) {
        return processingEnvironment.getTypeUtils().isAssignable(candidate, type);
    }

}
