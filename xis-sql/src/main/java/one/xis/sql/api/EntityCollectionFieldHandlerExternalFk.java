package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.ForeignKeyAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
abstract class EntityCollectionFieldHandlerExternalFk<E, F, EID> {

    private final Class<EID> entityPkType;
    private final Class<F> fieldClass;

    boolean isBeforeEntity() {
        return false;
    }

    void saveFieldValue(E entity) {
        EID pk = getEntityPk(entity);
        Collection<F> fieldValues = getFieldValue(entity);
        if (fieldValues == null) {
            fieldValues = Collections.emptyList();
        }
        updateExternal(pk, fieldValues);
    }

    void deleteFieldValue(E entity) {
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
        DeleteAction<F, EID> deleteAction = new DeleteAction<>(fieldClass, getForeignKeyName(), getEntityPk(entity));
        deleteAction.doDelete();
    }

    private void doSetFieldEntityFkToNullAction(E entity) {
        ColumnUpdateAction<F, EID> columnUpdateAction = new ColumnUpdateAction<>(fieldClass, getReferringKeyColumn(), entityPkType, getEntityPk(entity), null);
        columnUpdateAction.doUpdate();
    }


    private void updateExternal(EID pk, Collection<F> retainOrCreate) {
        ForeignTableUpdateAction<F, EID> action = new ForeignTableUpdateAction<>(fieldClass, getReferringKeyColumn(), pk, retainOrCreate);
        action.doUpdate();
    }

    protected abstract Collection<F> getFieldValue(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract void save(F fieldValue);

    protected abstract String getReferringKeyColumn();

    protected abstract String getForeignKeyName();

    protected abstract ForeignKeyAction getForeignKeyAction();

}
