package one.xis.sql.processor;

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    Class getCollectionWrapperType(TypeMirror collectionClass) {
        // order is important, here !
        throw new IllegalStateException("unsupported collection type " + collectionClass);
    }

    private boolean isAssignable(TypeMirror candidate, TypeMirror type) {
        return processingEnvironment.getTypeUtils().isAssignable(candidate, type);
    }

}
