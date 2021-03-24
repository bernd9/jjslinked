package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID, P extends EntityProxy<E, EID>> {

    private final EntityTableAccessor<E, EID, P> entityTableAccessor;


    public abstract void save(E entity);

    public void save(Collection<E> entities) {
        entities.forEach(this::save); // TODO this might be slow. Better implement similar logic again
    }

}
