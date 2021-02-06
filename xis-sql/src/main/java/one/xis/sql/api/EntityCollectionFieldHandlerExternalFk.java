package one.xis.sql.api;

import one.xis.sql.ForeignKeyAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

abstract class EntityCollectionFieldHandlerExternalFk<E, F, EID> {

    private final Class<EID> entityPkType;
    private final Class<F> fieldClass;
    private final ForeignTableUpdateAction<F, EID> foreignTableUpdateAction;
    private final ForeignKeyToNullAction<F, EID> foreignKeyToNullAction;
    private final DeleteAction<F, EID> deleteAction;

    protected EntityCollectionFieldHandlerExternalFk(Class<EID> entityPkType, Class<F> fieldClass) {
        this.entityPkType = entityPkType;
        this.fieldClass = fieldClass;
        this.foreignTableUpdateAction = new ForeignTableUpdateAction<>(fieldClass, entityPkType, getReferringKeyColumn());
        this.foreignKeyToNullAction = new ForeignKeyToNullAction<>(fieldClass, getReferringKeyColumn(), entityPkType);
        this.deleteAction = new DeleteAction<>(fieldClass, getForeignKeyName());
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
            if (getForeignKeyAction() == ForeignKeyAction.CASCADE_API) {
                doFieldEntityDeleteAction(entity);
            } else if (getForeignKeyAction() == ForeignKeyAction.SET_NULL_API) {
                doSetFieldEntityFkToNullAction(entity);
            }
        }
    }

    private void doFieldEntityDeleteAction(E entity) {
        deleteAction.doDelete(getEntityPk(entity));
    }

    private void doSetFieldEntityFkToNullAction(E entity) {
        foreignKeyToNullAction.doSetToNull(getEntityPk(entity));
    }

    private void updateExternal(EID pk, Collection<F> retainOrCreate) {
        foreignTableUpdateAction.doUpdate(pk, retainOrCreate);
    }

    protected abstract Collection<F> getFieldValue(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract void save(F fieldValue);

    protected abstract String getReferringKeyColumn();

    protected abstract String getForeignKeyName();

    protected abstract ForeignKeyAction getForeignKeyAction();

}
