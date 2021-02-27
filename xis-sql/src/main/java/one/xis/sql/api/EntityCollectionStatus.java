package one.xis.sql.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// TODO  THIS has to be a session value

@Deprecated
@Getter
class EntityCollectionStatus<E, EID> {
    private final List<E> removedValues = new ArrayList<>();
    private final List<E> newValues = new ArrayList<>();
    private final List<E> updatedValues = new ArrayList<>();
    private boolean dirty;

    void valueAdded(E entity) {
        dirty = true;
        if (entity instanceof EntityProxy) {
            EntityProxy<E, EID> entityProxy = (EntityProxy<E, EID>) entity;
            if (entityProxy.dirty()) {
                updatedValues.add(entity);
            }
        } else {
            newValues.add(entity);
        }
    }

    void valuesAdded(Iterable<E> entities) {
        dirty = true;
        entities.forEach(this::valueAdded);
    }

    void valueRemoved(E entity) {
        dirty = true;
        removedValues.add(entity);
    }

    void valuesRemoved(Collection<E> entities) {
        dirty = true;
        removedValues.addAll(entities);
    }

    void valueUpdated(E entity) {
        dirty = true;
    }
}
