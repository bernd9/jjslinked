package one.xis.sql.api.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityTableAccessor;

@Getter
@RequiredArgsConstructor
public class EntityDeleteAction<E> implements EntityAction {
    private final E entity;
    private final Class<E> entityClass;

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), entity);
    }

    @Override
    public EntityTableAccessor getEntityTableAccessor() {
        return null;
    }
}
