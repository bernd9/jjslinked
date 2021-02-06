package one.xis.sql.api;

import one.xis.sql.CrudRepository;
import one.xis.sql.ForeignKeyAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

abstract class EntityCollectionFieldHandlerExternalFk<E, F, EID, FID> {

    private final ForeignTableUpdateAction<EID, F, FID> foreignTableUpdateAction;
    private final ForeignKeyOnDeleteAction<EID, FID> foreignKeyOnDeleteAction;

    protected EntityCollectionFieldHandlerExternalFk(Class<EID> entityPkType, Class<F> fieldClass,
                                                     ForeignKeyAccessor<EID, FID> foreignKeyAccessor,
                                                     CrudRepository<F, FID> fieldCrudRepository) {
        this.foreignTableUpdateAction = new ForeignTableUpdateAction<>(getForeignKeyAction(),
                getGetFieldPkFunction(),
                getSetFieldFkBiConsumer(),
                foreignKeyAccessor,
                fieldCrudRepository);
        this.foreignKeyOnDeleteAction = new ForeignKeyOnDeleteAction<>(getForeignKeyAction(), foreignKeyAccessor);
    }

    boolean isBeforeEntity() {
        return false;
    }

    void onSaveEntity(E entity) {
        EID pk = getEntityPk(entity);
        Collection<F> fieldValues = getFieldValue(entity);
        if (fieldValues == null) {
            fieldValues = Collections.emptyList();
        }
        updateExternal(pk, fieldValues);
    }

    void onDeleteEntity(E entity) {
        Objects.requireNonNull(getEntityPk(entity), "trying to delete non-persistent entity " + entity);
        Collection<F> fieldValue = getFieldValue(entity);
        if (fieldValue != null) {
            foreignKeyOnDelete(entity);
        }
    }

    private void foreignKeyOnDelete(E entity) {
        foreignKeyOnDeleteAction.doAction(getEntityPk(entity));
    }

    private void updateExternal(EID pk, Collection<F> retainOrCreate) {
        foreignTableUpdateAction.doUpdate(pk, retainOrCreate);
    }

    protected abstract Collection<F> getFieldValue(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract void save(F fieldValue);

    protected abstract ForeignKeyAction getForeignKeyAction();

    protected abstract Function<F, FID> getGetFieldPkFunction();

    protected abstract BiConsumer<F, EID> getSetFieldFkBiConsumer();


}
