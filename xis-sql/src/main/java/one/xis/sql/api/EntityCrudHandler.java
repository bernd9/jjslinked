package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public abstract class EntityCrudHandler<E, EID> {

    private final EntityTableAccessor<E, EID> entityTableAccessor;


    public void save(E e) {
        if (!CrudHandlerSession.getInstance().hasSaveAction(e)) {
            doSave(e);
        }
    }

    protected abstract void doSave(E entity);

    public void save(Collection<E> entities) {
        entities.forEach(this::save); // TODO this might be slow. Better implement similar logic again
    }



}
