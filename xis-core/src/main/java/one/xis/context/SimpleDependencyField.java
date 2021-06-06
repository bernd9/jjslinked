package one.xis.context;

import one.xis.util.FieldUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleDependencyField {

    private final String name;

    @Getter
    private final ClassReference fieldType;
    private Object value;

    public void onSingletonCreated(Object o) {
        if (fieldType.isInstance(o)) {
            if (value != null) {
                // TODO throw exception
            }
            value = o;
        }
    }

    void setFieldValue(Object owner) {
        FieldUtils.setFieldValue(owner, name, value);
    }

    boolean isSatisfied() {
        return value != null;
    }
}
