package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID, P extends EntityProxy<E, EID>> {

    private final EntityTableAccessor<E, EID, P> entityTableAccessor;

}
