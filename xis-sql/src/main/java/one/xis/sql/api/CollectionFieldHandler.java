package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.collection.EntityCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
abstract class CollectionFieldHandler<E, EID, F, FID> {

    private final EntityTableAccessor<F, FID> fieldEntityTableAccessor;
    private final EntityFunctions<F, FID> fieldEntityFunctions;
    private final Class<E> entityType;
    private final Class<F> fieldType;
    
    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public void updateFieldValues(E entity, F fieldValue, EntityCrudHandlerSession session) {
        updateFieldValues(entity, Collections.singleton(fieldValue), session);
    }

    @UsedInGeneratedCode
    public void updateFieldValues(E entity, Collection<F> fieldValues, EntityCrudHandlerSession crudHandlerSession) {
        Session.required();
        if (fieldValues instanceof EntityCollection) {
            updateCollectionProxyFieldValues(entity, (EntityCollection) fieldValues, crudHandlerSession);
            return;
        }
        geCloneFromSession(entity)
                .ifPresentOrElse(clone -> updateFieldValuesBySessionState(entity, clone, crudHandlerSession),
                        () -> updateFieldValuesForNewEntity(entity, crudHandlerSession));
    }

    private Optional<E> geCloneFromSession(E actual) {
        return Session.getInstance().getCloneFromSession(actual);
    }

    private void updateFieldValuesBySessionState(E actualEntity, E sessionEntity, EntityCrudHandlerSession crudHandlerSession) {
        updateFieldValuesBySessionState(actualEntity, getFieldValues(actualEntity), getFieldValues(sessionEntity), crudHandlerSession);
    }

    private void updateFieldValuesForNewEntity(E actualEntity, EntityCrudHandlerSession crudHandlerSession) {
        getFieldValues(actualEntity).stream()
                .peek(fieldValue -> updateLinkColumnValue(fieldValue, actualEntity))
                .forEach(fieldValue -> addSaveAction(fieldValue, crudHandlerSession));
    }

    private void updateFieldValuesBySessionState(E entity, Collection<F> actualFieldValues, Collection<F> sessionFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        Map<Integer, F> sessionFieldValueMap = sessionFieldValues.stream().collect(Collectors.toMap(System::identityHashCode, Function.identity()));
        Map<Integer, F> actualFieldValueMap = actualFieldValues.stream().peek(fieldValue -> updateLinkColumnValue(fieldValue, entity)).collect(Collectors.toMap(System::identityHashCode, Function.identity()));
        handleUnlinkedFieldValues(getEntityPk(entity), getUnlinkedValues(sessionFieldValueMap, actualFieldValueMap), crudHandlerSession);
        saveFieldEntities(actualFieldValueMap.values(), crudHandlerSession);
    }

    private void saveFieldEntities(Collection<F> actualFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        actualFieldValues.forEach(fieldValue -> addSaveAction(fieldValue, crudHandlerSession));
    }

    private void addSaveAction(F fieldValue, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addSaveAction(fieldValue, fieldEntityTableAccessor, fieldEntityFunctions);
    }

    private void addBulkUpdateAction(Collection<F> fieldValues, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addBulkUpdateAction(fieldValues, fieldEntityTableAccessor, fieldEntityFunctions);
    }

    private void addBulkInsertAction(Collection<F> fieldValues, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addBulkInsertAction(fieldValues, fieldEntityTableAccessor, fieldEntityFunctions);
    }

    private Collection<F> getUnlinkedValues(Map<Integer, F> sessionFieldValueMap, Map<Integer, F> actualFieldValueMap) {
        return sessionFieldValueMap.keySet().stream()
                .filter(sessionHashCode -> !actualFieldValueMap.containsKey(sessionHashCode))
                .map(sessionFieldValueMap::get).collect(Collectors.toSet());
    }


    private void updateCollectionProxyFieldValues(E entity, EntityCollection<F> entityCollection, EntityCrudHandlerSession crudHandlerSession) {
        if (entityCollection.isDirty()) {
            EID entityId = getEntityPk(entity);
            handleUnlinkedFieldValues(entityId, entityCollection.getUnlinkedValues(), crudHandlerSession);
            addBulkUpdateAction(entityCollection.getDirtyValues(), crudHandlerSession);
            addBulkInsertAction(entityCollection.getNewValues(), crudHandlerSession);
        }
    }

    protected abstract Collection<F> getFieldValues(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract void handleUnlinkedFieldValues(EID entityId, Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession);

    protected abstract void updateLinkColumnValue(F fieldValue, E entity);
}
