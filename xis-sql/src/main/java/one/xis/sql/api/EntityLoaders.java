package one.xis.sql.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityLoaders {

    public static <E> EntityLoader<E> loaderForType(Class<E> c) {
        return null;
    }
}
