package one.xis.sql.api;

import one.xis.context.UsedInGeneratedCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.collection.EntityCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .forEach(fieldValue -> addSaveAction(actualEntity, fieldValue, crudHandlerSession));
    }

    private void updateCollectionProxyFieldValues(E entity, EntityCollection<F> entityCollection, EntityCrudHandlerSession crudHandlerSession) {
        if (entityCollection.isDirty()) {
            EID entityId = getEntityPk(entity);
            handleUnlinkedFieldValues(entityId, entityCollection.getUnlinkedValues(), crudHandlerSession);
            addBulkUpdateAction(entity, entityCollection.getDirtyValues().stream(), crudHandlerSession, entityCollection.getElementType());
            addBulkInsertAction(entity, entityCollection.getNewValues().stream(), crudHandlerSession, entityCollection.getElementType());
        }
    }

    private void updateFieldValuesBySessionState(E entity, Collection<F> actualFieldValues, Collection<F> sessionFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        Map<Integer, F> sessionFieldValueMap = sessionFieldValues.stream().collect(Collectors.toMap(System::identityHashCode, Function.identity()));
        Map<Integer, F> actualFieldValueMap = actualFieldValues.stream().collect(Collectors.toMap(System::identityHashCode, Function.identity()));
        handleUnlinkedFieldValues(getEntityPk(entity), getUnlinkedValues(sessionFieldValueMap, actualFieldValueMap), crudHandlerSession);
        doUpdateFieldValuesBySessionState(entity, sessionFieldValueMap, actualFieldValueMap, crudHandlerSession);
    }

    protected void doUpdateFieldValuesBySessionState(E entity, @SuppressWarnings("unused") Map<Integer, F> sessionFieldValueMap,  Map<Integer, F> actualFieldValueMap, EntityCrudHandlerSession crudHandlerSession) {
        saveFieldEntities(entity, actualFieldValueMap.values().stream(), crudHandlerSession);
    }

    private void saveFieldEntities(E entity, Stream<F> actualFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        actualFieldValues.forEach(fieldValue -> addSaveAction(entity, fieldValue, crudHandlerSession));
    }

    protected void addSaveAction(E entity, F fieldValue, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addSaveAction(fieldValue, fieldEntityTableAccessor, fieldEntityFunctions);
    }

    protected void addBulkUpdateAction(@SuppressWarnings("unused") E entity, Stream<F> fieldValues, EntityCrudHandlerSession crudHandlerSession, Class<F> fieldType) {
        crudHandlerSession.addBulkUpdateAction(fieldValues, fieldEntityTableAccessor, fieldEntityFunctions, fieldType);
    }

    protected void addBulkInsertAction(@SuppressWarnings("unused") E entity, Stream<F> fieldValues, EntityCrudHandlerSession crudHandlerSession, Class<F> fieldType) {
        crudHandlerSession.addBulkInsertAction(fieldValues, fieldEntityTableAccessor, fieldEntityFunctions, fieldType);
    }

    private Collection<F> getUnlinkedValues(Map<Integer, F> sessionFieldValueMap, Map<Integer, F> actualFieldValueMap) {
        return sessionFieldValueMap.keySet().stream()
                .filter(sessionHashCode -> !actualFieldValueMap.containsKey(sessionHashCode))
                .map(sessionFieldValueMap::get).collect(Collectors.toSet());
    }

    protected abstract Collection<F> getFieldValues(E entity);

    protected abstract EID getEntityPk(E entity);

    protected abstract void handleUnlinkedFieldValues(EID entityId, Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession);


}
