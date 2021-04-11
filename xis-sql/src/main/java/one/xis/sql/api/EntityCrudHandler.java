package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.collection.EntityCollection;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID> {

    private final EntityTableAccessor<E, EID> entityTableAccessor;
    private final EntityFunctions<E, EID> entityFunctions;

    public void save(E entity) {
        EntityCrudHandlerSession session = new EntityCrudHandlerSession();
        save(entity, session);
        session.executeActions();
    }

    public void save(E entity, EntityCrudHandlerSession session) {
        if (!session.hasSaveAction(entity)) {
            doSave(entity, session);
        }
    }

    public void save(Collection<E> entities) {
        EntityCrudHandlerSession session = new EntityCrudHandlerSession();
        save(entities, session);
        session.executeActions();
    }

    public void save(Collection<E> entities, EntityCrudHandlerSession session) {
        if (entities instanceof EntityCollection) {
            EntityCollection<E> entityCollection = (EntityCollection<E>) entities;
            session.addBulkUpdateAction(entityCollection.getDirtyValues().stream(), entityTableAccessor, entityFunctions, entityCollection.getElementType());
        } else {
            entities.forEach(e -> save(e, session));
        }
    }

    public Optional<E> findById(EID id) {
        return entityTableAccessor.findById(id);
    }

    // TODO : better stream result-set by wrapping it in a stream ?
    public List<E> findAll() {
        return entityTableAccessor.findAll();
    }

    public void delete(E entity) {
       // TODO
    }

    public void deleteAll(Collection<E> entities) {
        // TODO
    }

    protected abstract void doSave(E entity, EntityCrudHandlerSession session);

}
