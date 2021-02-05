package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
class ForeignTableUpdateAction<E, K> {
    private final Class<E> updateEntity;
    private final String foreignKeyColumnName;
    private final K foreignKeyValue;
    private final Collection<E> retainOrCreate;

    void doUpdate() {
        // TODO not forget to save new items in "retain"
        // TODO before deleting check if there ist a constraint for this matter
    }
}
