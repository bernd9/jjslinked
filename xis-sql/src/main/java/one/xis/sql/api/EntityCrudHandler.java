package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.action.EntityAction;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID> {

    private final EntityTableAccessor<E, EID> entityTableAccessor;
    private final EntityFunctions<E,EID> entityFunctions;

    public void save(E e) {
        EntityCrudHandlerSession session = new EntityCrudHandlerSession();
        save(e, session);
        session.executeActions();
    }

    public void save(E e, EntityCrudHandlerSession session) {
        if (!session.hasSaveAction(e)) {
            doSave(e, session);
        }
    }

    protected abstract void doSave(E entity, EntityCrudHandlerSession session);

    public void save(Collection<E> entities) {
        entities.forEach(this::save); // TODO this might be slow. Better implement similar logic again
    }



}
