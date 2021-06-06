package one.xis.context;

import java.lang.reflect.Constructor;
import java.util.List;

class SingletonConstructor extends SingletonProvider {

    public SingletonConstructor(ClassReference type, List<ParameterReference> parameterReferences) {
        super(type, parameterReferences);
    }

    @Override
    Object provide() {
        try {
            Constructor<?> constructor = getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
            constructor.setAccessible(true);
            return constructor.newInstance(parameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
