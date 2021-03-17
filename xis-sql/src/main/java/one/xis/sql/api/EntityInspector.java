package one.xis.sql.api;

import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.action.EntityActions;
import one.xis.sql.api.action.EntityInsertAction;
import one.xis.sql.api.action.EntityUpdateAction;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class EntityInspector<E, EID> {
    private final Class<E> entityClass;
    private final EntityActions entityActions;

    void createActionFor(E actual) {
        if (actual instanceof EntityProxy) {
            EntityProxy proxy = (EntityProxy) actual;
            if (proxy.dirty()) {
                entityActions.add(new EntityUpdateAction<E>((E)proxy, entityClass));
            }
        }
        E old = getOldStateFromSession(getPk(actual));
        if (old == null) {
            entityActions.add(new EntityInsertAction<>(actual, entityClass));
        } else if (areEqual(old, actual)) {
            entityActions.add(new EntityUpdateAction<>(actual, entityClass));
        }
    }

    protected Set<E> createActionFor(Collection<E> oldCollection, Collection<E> actualCollection) {
        Set<E> removed = new HashSet<>();
        Map<EID,E> oldMap = asMap(oldCollection);
        Map<EID,E> actualMap = asMap(actualCollection);
        for (Map.Entry<EID,E> e : oldMap.entrySet()) {
            if (!actualMap.containsKey(e.getKey())) {
               removed.add(e.getValue());
            }
        }
        for (Map.Entry<EID,E> e : actualMap.entrySet()) {
            E old = oldMap.get(e.getKey());
            E actual = e.getValue();
            if (old == null) {
                old = getOldStateFromSession(e.getKey());
            }
            createActionFor(old, actual);
        }
        return removed;
    }

    private Map<EID,E> asMap(Collection<E> entities) {
        return entities.stream().collect(Collectors.toMap(this::getPk, Functions.identity()));
    }

    protected abstract E getOldStateFromSession(EID pk);

    protected abstract boolean areEqual(E old, E actual);

    protected abstract EID getPk(E entity);
}
