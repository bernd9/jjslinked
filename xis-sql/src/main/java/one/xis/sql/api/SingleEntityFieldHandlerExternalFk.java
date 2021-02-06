package one.xis.sql.api;

import one.xis.sql.CrudRepository;
import one.xis.sql.ForeignKeyAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Base class of generated handlers for an entity-field with foreign key in table of
 * the field's type.
 *
 * @param <E>   type of entity, which is owner of the field we deal with, here
 * @param <F>   field type, which ist also an entity
 * @param <EID> primary key type of of E
 * @param <FID> primary key type of of F
 */
abstract class SingleEntityFieldHandlerExternalFk<E, F, EID, FID> {

    private final ForeignTableUpdateAction<EID, F, FID> foreignTableUpdateAction;
    private final ForeignKeyOnDeleteAction<EID, FID> foreignKeyOnDeleteAction;

    protected SingleEntityFieldHandlerExternalFk(ForeignKeyAccessor<EID, FID> foreignKeyAccessor, CrudRepository<F, FID> fieldCrudRepository) { // TODO in subtype let inject concrete ReposotyImpl
        this.foreignTableUpdateAction = new ForeignTableUpdateAction<>(getForeignKeyAction(), getGetFieldPkFunction(), getSetFieldFkBiConsumer(), foreignKeyAccessor, fieldCrudRepository);
        this.foreignKeyOnDeleteAction = new ForeignKeyOnDeleteAction<>(getForeignKeyAction(), foreignKeyAccessor);
    }

    boolean isBeforeEntity() {
        return false;
    }

    void onSaveEntity(E entity) {
        EID pk = getEntityPk(entity); // TODO check in foreign keys for type to be equal
        Objects.requireNonNull(pk);
        Collection<F> fieldValues = Optional.ofNullable(getFieldValue(entity))
                .map(Collections::singleton).orElse(Collections.emptySet());
        updateExternal(pk, fieldValues);
    }

    void onDeleteEntity(E entity) {
        Objects.requireNonNull(getEntityPk(entity), "trying to delete non-persistent entity " + entity);
        F fieldValue = getFieldValue(entity);
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

    protected abstract F getFieldValue(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract ForeignKeyAction getForeignKeyAction();

    protected abstract Function<F, FID> getGetFieldPkFunction();

    protected abstract BiConsumer<F, EID> getSetFieldFkBiConsumer();

}
