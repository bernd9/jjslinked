package one.xis.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class SimpleParameter implements Parameter {

    private final ClassReference parameterType;
    private Object value;

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return value != null;
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (parameterType.isInstance(o)) {
            if (value != null) {
                // TODO throw exception
            }
            value = o;
        }
    }
}
