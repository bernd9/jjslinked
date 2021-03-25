package one.xis.sql.api.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityTableAccessor;

@Getter
@RequiredArgsConstructor
public class EntitySaveAction<E> implements EntityAction<E> {
    private final E entity;
    private final EntityTableAccessor<E,?> entityTableAccessor;

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), entity);
    }

}
