package one.xis.sql.api.collection;

import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class EntityCollections {
    public <C extends Collection<E>, E> C getCollection(Class<C> collectionType) {
        return null; // TODO
    }
}
