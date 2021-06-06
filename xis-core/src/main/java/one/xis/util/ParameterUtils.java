package one.xis.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

@UtilityClass
public class ParameterUtils {
    // TODO this is a type parameter of a collection, rename method
    public static Optional<Class<?>> getGenericCollectionType(Parameter parameter) {
        if (!Collection.class.isAssignableFrom(parameter.getType())) {
            throw new IllegalArgumentException("not a collection parameter: " + parameter);
        }
        Type genericType = parameter.getParameterizedType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            return Optional.of((Class<?>) parameterizedType.getActualTypeArguments()[0]);
        }
        return Optional.empty();
    }
}
