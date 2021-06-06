package one.xis.context;

import com.ejc.util.FieldUtils;
import com.ejc.util.TypeUtils;
import lombok.Getter;

import java.util.Collection;

@Getter
class CollectionDependencyField extends SingletonCollection {

    private final String name;
    private final ClassReference declaringType;
    private final ClassReference fieldType;
    private final Collection<Object> fieldValues;

    CollectionDependencyField(String name, ClassReference declaringType, ClassReference fieldType) {
        super(fieldType.getGenericType().orElseThrow());
        this.name = name;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldValues = TypeUtils.emptyCollection((Class<? extends Collection<Object>>) fieldType.getReferencedClass());
    }

    void setFieldValue(Object owner) {
        FieldUtils.setFieldValue(owner, name, fieldValues);
    }

    void onSingletonCreated(Object o) {
        if (getElementType().isInstance(o)) {
            fieldValues.add(o);
        }
    }
}
