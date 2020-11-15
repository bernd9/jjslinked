package com.ejc.api.context;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Das Setzen der neu erzeugten Singletons und der Aufruf der Provider
 * müsste vielleicht getrennt laufen.
 * <p>
 * Wenn alle Provider verbraucht sind ist Schluß oder so äähnlich.
 */

class SingletonConstructor extends SingletonProvider {

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, parameterTypes);
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
