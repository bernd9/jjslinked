package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID, P extends EntityProxy<E, EID>> {

    private final EntityTableAccessor<E, EID, P> entityTableAccessor;


    public void save(E entity) {

    }

    // TODO return collection not needed -> remove return value ?
    public Collection<E> save(Collection<E> entities) {
        return null;
    }

}
