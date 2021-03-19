package one.xis.sql.api;

import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID, P extends EntityProxy<E, EID>> {

    private final EntityTableAccessor<E, EID, P> entityTableAccessor;

    public void save(E entity) {
        entityTableAccessor.save(entity);

    }


    protected <F, FID> void updateReferencedField(Collection<F> values, Class<F> fieldType, Class<FID> fieldPkType) {

    }

    protected void updateCrossTableField() {

    }


    // protected abstract EntityProxy<E, ID> createProxy(E entity);

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
            //createActionFor(old, actual);
        }
        return removed;
    }

    protected abstract E getOldStateFromSession(EID key);

    private Map<EID,E> asMap(Collection<E> entities) {
        return entities.stream().collect(Collectors.toMap(this::getPk, Functions.identity()));
    }

    private <K, T> K getPk(T t) {
        return null;
    }

}
