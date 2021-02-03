package one.xis.sql.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;


class EntityCollectionWrappers {

    private final ProcessingEnvironment processingEnvironment;
    private final TypeMirror collectionTypeMirror;
    private final TypeMirror setTypeMirror;
    private final TypeMirror listTypeMirror;
    private final TypeMirror arrayListTypeMirror;
    private final TypeMirror linkedListTypeMirror;
    private final TypeMirror hashSetTypeMirror;

    EntityCollectionWrappers(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
        this.collectionTypeMirror = typeMirror(Collection.class);
        this.setTypeMirror = typeMirror(Set.class);
        this.listTypeMirror = typeMirror(List.class);
        this.arrayListTypeMirror = typeMirror(ArrayList.class);
        this.linkedListTypeMirror = typeMirror(LinkedList.class);
        this.hashSetTypeMirror = typeMirror(HashSet.class);
    }

    private TypeMirror typeMirror(Class<?> c) {
        return processingEnvironment.getElementUtils().getTypeElement(c.getName()).asType();
    }


    <C extends Collection<?>> C getCollectionWrapper(TypeMirror collectionClass) {
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, collectionTypeMirror)) {

        }
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, setTypeMirror)) {

        }
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, listTypeMirror)) {

        }
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, arrayListTypeMirror)) {

        }
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, linkedListTypeMirror)) {

        }
        if (processingEnvironment.getTypeUtils().isAssignable(collectionClass, hashSetTypeMirror)) {

        }
        return null;
    }

    class SetWrapper {
        private Set set;
    }

}
