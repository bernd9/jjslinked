package one.xis.sql.api;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class ReferredCrudHandlerFieldHandler<E, EID, F, FID> extends CollectionFieldHandler<E, EID, F, FID> {

    public ReferredCrudHandlerFieldHandler(EntityTableAccessor<F, FID> fieldEntityTableAccessor, EntityFunctions<F, FID> fieldEntityFunctions, Class<E> entityType, Class<F> fieldType) {
        super(fieldEntityTableAccessor, fieldEntityFunctions, entityType, fieldType);
    }

    protected void unlinkBySetFkToNull(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        unlinkedFieldValues.forEach(fieldValue -> addValueUpdateAction(fieldValue, crudHandlerSession));
    }

    protected void unlinkByDelete(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        addBulDeleteAction(unlinkedFieldValues, crudHandlerSession);
    }

    private void addBulDeleteAction(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addBulkDeleteAction(unlinkedFieldValues.stream(), getFieldEntityTableAccessor(), getFieldEntityFunctions(), getFieldType());
    }

    private void addValueUpdateAction(F fieldValue, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addValueUpdateAction(fieldValue, getForeignKeyToNullConsumer(), getFieldEntityTableAccessor(), getFieldEntityFunctions());
    }

    protected Consumer<F> getForeignKeyToNullConsumer() {
        return fieldValue -> setFieldValueFk(fieldValue, null);
    }

    private void updateForeignKeyColumnValue(F fieldValue, E entity) {
        setFieldValueFk(fieldValue, entity);
    }

    @Override
    protected void addSaveAction(E entity, F fieldValue, EntityCrudHandlerSession crudHandlerSession) {
        updateForeignKeyColumnValue(fieldValue, entity);
        super.addSaveAction(entity, fieldValue, crudHandlerSession);
    }

    @Override
    protected void addBulkInsertAction(E entity, Stream<F> fieldValues, EntityCrudHandlerSession crudHandlerSession, Class<F> fieldType) {
        super.addBulkInsertAction(entity, fieldValues.peek(value -> updateForeignKeyColumnValue(value, entity)), crudHandlerSession, fieldType);
    }

    @Override
    protected void addBulkUpdateAction(E entity, Stream<F> fieldValues, EntityCrudHandlerSession crudHandlerSession, Class<F> fieldType) {
        super.addBulkUpdateAction(entity, fieldValues.peek(value -> updateForeignKeyColumnValue(value, entity)), crudHandlerSession, fieldType);
    }

    protected abstract void setFieldValueFk(F fieldValue, E foreignKey);

}
