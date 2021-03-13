package one.xis.sql.api;

import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.action.EntityAction;
import one.xis.sql.api.action.EntityActions;
import one.xis.sql.api.action.EntityInsertAction;
import one.xis.sql.api.action.EntityUpdateAction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class EntityComparison<E, EID> {
    private final Class<E> entityClass;
    private final EntityActions entityActions;


    void shallowCompare(E old, E actual) {
        if (actual instanceof EntityProxy) {
            EntityProxy proxy = (EntityProxy) actual;
            if (proxy.dirty()) {
                entityActions.add(new EntityUpdateAction<E>((E)proxy, entityClass));
            }
        } else if (old == null) {
            entityActions.add(new EntityInsertAction<>(actual, entityClass));
        } else if (areEqual(old, actual)) {
            entityActions.add(new EntityUpdateAction<>(actual, entityClass));
        }
    }

    void shallowCompare(Collection<E> oldCollection, Collection<E> actualCollection, Function<E, EntityAction> addActionMapper, Function<E, EntityAction> removeActionMapper) {
        Map<EID,E> oldMap = asMutableMap(oldCollection);
        Map<EID,E> actualMap = asMutableMap(actualCollection);
        for (Map.Entry<EID,E> e : oldMap.entrySet()) {
            if (!actualMap.containsKey(e.getKey())) {
                entityActions.add(removeActionMapper.apply(e.getValue()));
            }
        }
        for (Map.Entry<EID,E> e : actualMap.entrySet()) {
            E old = oldMap.get(e.getKey());
            if (old == null) {
                entityActions.add(addActionMapper.apply(e.getValue()));
            } else {
                shallowCompare(old, e.getValue());
            }
        }
    }

    private Map<EID,E> asMutableMap(Collection<E> entities) {
        return entities.stream().collect(Collectors.collectingAndThen(Collectors.toMap(this::getPk, Functions.identity()), m -> new HashMap<>(m)));
    }


    protected abstract boolean areEqual(E old, E actual);

    protected abstract EID getPk(E entity);
}
