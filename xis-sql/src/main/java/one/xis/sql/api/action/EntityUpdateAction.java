package one.xis.sql.api.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EntityUpdateAction<E> implements EntityAction {
    private final E entity;
    private final Class<E> entityClass;

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), entity);
    }
}
