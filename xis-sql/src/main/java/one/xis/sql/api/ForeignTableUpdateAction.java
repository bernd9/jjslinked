package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
class ForeignTableUpdateAction<F, K> {
    private final Class<F> updateEntity;
    private final Class<K> foreignKeyType;
    private final String foreignKeyColumnName;

    void doUpdate(@NonNull K foreignKeyValue, @NonNull Collection<F> retainOrCreate) {

        // TODO not forget to save new items in "retain"
        // TODO before deleting check if there ist a constraint for this matter
    }


    private void doUpdate(F entity) {

    }

}
