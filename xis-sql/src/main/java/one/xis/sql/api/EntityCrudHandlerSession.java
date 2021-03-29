package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.Data;

import java.util.*;
import java.util.function.Consumer;

public class EntityCrudHandlerSession {

    // Do not replace this by a set. Order is important here.
    private List<EntityActions> actionsForEntityTypes = new ArrayList<>();

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public boolean hasSaveAction(Object o) {
        Class<?> entityClass = o.getClass();
        return findExistingEntityActions(entityClass).map(a -> a.hasSaveAction(o)).orElse(false);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public boolean hasDeleteAction(Object o) {
        Class<?> entityClass = o.getClass();
        return findExistingEntityActions(entityClass).map(a -> a.hasDeleteAction(o)).orElse(false);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addSaveAction(Object o, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addEntityToSave(o);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addDeleteAction(Object o, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addEntityToDelete(o);
    }

    @SuppressWarnings("unchecked")
    public void addValueUpdateAction(Object o, Consumer<Object> valueUpdater, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addEntityForFieldUpdate(o, valueUpdater);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addBulkUpdateAction(List<?> entities, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        if (!entities.isEmpty()) {
            Class<?> entityClass = entities.get(0).getClass();
            entityActionsForType(entityClass, tableAccessor, functions).addBulkUpdateAction((Collection<Object>) entities);
        }
    }


    void executeActions() {
        actionsForEntityTypes.forEach(EntityActions::executeActions);
    }

    void clear() {
        actionsForEntityTypes.clear();
    }

    private EntityActions entityActionsForType(Class<?> entityType, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        return findExistingEntityActions(entityType).orElseGet(() -> createNewEntityActions(entityType, tableAccessor, functions));
    }

    @SuppressWarnings("unchecked")
    private Optional<EntityActions> findExistingEntityActions(Class<?> entityType) {
        return actionsForEntityTypes.stream()
                .filter(actions -> actions.matches(entityType))
                .findFirst();
    }

    private EntityActions createNewEntityActions(Class<?> entityType, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        EntityActions entityActions = new EntityActions(entityType, (EntityTableAccessor<Object, Object>) tableAccessor, functions);
        actionsForEntityTypes.add(entityActions);
        return entityActions;
    }


    @Data
    private static class EntityActions {

        private final Class<?> entityType;
        private final EntityTableAccessor<Object, Object> entityTableAccessor;
        private final EntityFunctions<?, ?> functions;

        private final Map<Integer, Object> insertEntities = new HashMap<>();
        private final Map<Integer, Object> updateEntities = new HashMap<>();
        private final Map<Integer, Object> deleteEntities = new HashMap<>();

        boolean hasSaveAction(Object o) {
            int hashCode = System.identityHashCode(o);
            return insertEntities.containsKey(hashCode) || updateEntities.containsKey(hashCode);
        }

        boolean hasDeleteAction(Object o) {
            return deleteEntities.containsKey(System.identityHashCode(o));
        }

        boolean matches(Class<?> type) {
            return entityType.equals(type);
        }

        void addEntityToSave(Object o) {
            // replace previous action
            int hashCode = System.identityHashCode(o);
            if (o instanceof EntityProxy) {
                EntityProxy<?, ?> entityProxy = (EntityProxy<?, ?>) o;
                if (entityProxy.dirty()) {
                    if (updateEntities.containsKey(hashCode)) {
                        throw new IllegalStateException();
                    }
                    updateEntities.put(hashCode, entityProxy);
                    return;
                }
            }
            EntityState entityState = Session.getInstance().getEntityState(o, functions);
            switch (entityState) {
                case NEW:
                    if (insertEntities.containsKey(hashCode)) {
                        throw new IllegalStateException();
                    }
                    insertEntities.put(hashCode, o);
                    break;
                case EDITED:
                    if (updateEntities.containsKey(hashCode)) {
                        throw new IllegalStateException();
                    }
                    updateEntities.put(hashCode, o);
                    break;
                case UNCHANGED:
                    // NOOP
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        void addEntityToDelete(Object o) {
            // replace previous action
            deleteEntities.put(System.identityHashCode(o), o);
        }

        void addEntityForFieldUpdate(Object o, Consumer<Object> fieldUpdater) {
            int hashCode = System.identityHashCode(o);
            if (updateEntities.containsKey(hashCode)) {
                fieldUpdater.accept(updateEntities.get(hashCode));
                return;
            }
            if (insertEntities.containsKey(hashCode)) {
                fieldUpdater.accept(insertEntities.get(hashCode));
                return;
            }
            fieldUpdater.accept(o);
            updateEntities.put(hashCode, o);
        }

        void addBulkUpdateAction(Collection<Object> entities) {
           entities.forEach(e -> updateEntities.put(System.identityHashCode(e), e));
        }

        void executeActions() {
            entityTableAccessor.delete(deleteEntities.values());
            entityTableAccessor.insert(insertEntities.values());
            entityTableAccessor.update(updateEntities.values());
        }
    }

}
