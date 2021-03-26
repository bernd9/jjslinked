package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.Data;
import one.xis.sql.api.action.EntityAction;
import one.xis.sql.api.action.EntityDeleteAction;
import one.xis.sql.api.action.EntitySaveAction;

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
    public void addSaveAction(Object o, EntityTableAccessor<?,?> tableAccessor, EntityFunctions<?,?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addSaveAction(o);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addDeleteAction(Object o, EntityTableAccessor<?,?> tableAccessor, EntityFunctions<?,?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addDeleteAction(o);
    }

    @SuppressWarnings("unchecked")
    public void addValueUpdateAction(Object o, Consumer<Object> valueUpdater, EntityTableAccessor<?,?> tableAccessor, EntityFunctions<?,?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addValueUpdateAction(o, valueUpdater);
    }


    void executeActions() {
        actionsForEntityTypes.forEach(EntityActions::executeActions);
    }

    void clear() {
        actionsForEntityTypes.clear();
    }

    private EntityActions entityActionsForType(Class<?> entityType, EntityTableAccessor<?,?> tableAccessor, EntityFunctions<?,?> functions) {
        return findExistingEntityActions(entityType).orElseGet(() -> createNewEntityActions(entityType, tableAccessor, functions));
    }

    @SuppressWarnings("unchecked")
    private Optional<EntityActions> findExistingEntityActions(Class<?> entityType) {
        return actionsForEntityTypes.stream()
                .filter(actions -> actions.matches(entityType))
                .findFirst();
    }

    private EntityActions createNewEntityActions(Class<?> entityType, EntityTableAccessor<?,?> tableAccessor, EntityFunctions<?,?> functions) {
        EntityActions entityActions = new EntityActions(entityType, (EntityTableAccessor<Object, Object>) tableAccessor, functions);
        actionsForEntityTypes.add(entityActions);
        return entityActions;
    }

    @Data
    private static class EntityActions {

        private final Class<?> entityType;
        private final EntityTableAccessor<Object,Object> entityTableAccessor;
        private final EntityFunctions<?,?> functions;
        private Map<Integer, EntityAction<Object>> entityActions = new HashMap<>();

        boolean hasSaveAction(Object o) {
            return Optional.ofNullable(entityActions.get(System.identityHashCode(o)))
                    .map(EntitySaveAction.class::isInstance)
                    .orElse(false);
        }

        boolean hasDeleteAction(Object o) {
            return Optional.ofNullable(entityActions.get(System.identityHashCode(o)))
                    .map(EntityDeleteAction.class::isInstance)
                    .orElse(false);
        }

        boolean matches(Class<?> type) {
            return entityType.equals(type);
        }

        void addSaveAction(Object o) {
            // replace previous action
            entityActions.put(System.identityHashCode(o), new EntitySaveAction<>(o));
        }

        void addDeleteAction(Object o) {
            // replace previous action
            entityActions.put(System.identityHashCode(o), new EntityDeleteAction<>(o));
        }

        void addValueUpdateAction(Object o, Consumer<Object> valueUpdater) {
            int hashCode = System.identityHashCode(o);
            EntityAction<Object> action = entityActions.get(hashCode);
            if (action == null) {
                valueUpdater.accept(o);
                entityActions.put(hashCode, new EntitySaveAction<>(o));
            } else if (action instanceof EntitySaveAction) {
                valueUpdater.accept(action.getEntity());
            } else if (action instanceof EntityDeleteAction){
                throw new IllegalStateException("you are trying to update deleted entity " + o);
            } else {
                throw new IllegalStateException("unknown action: " + action);
            }
        }

        void executeActions() {
            Collection<Object> insertEntities = new HashSet<>();
            Collection<Object> updateEntities = new HashSet<>();
            Collection<Object> deleteEntities = new HashSet<>();

            for (EntityAction<Object> action : entityActions.values()) {
                Object entity = action.getEntity();
                if (action instanceof EntitySaveAction) {
                    SqlSaveAction sqlSaveAction = Session.getInstance().getSaveAction(entity, functions);
                    switch (sqlSaveAction) {
                        case INSERT: insertEntities.add(entity);
                        break;
                        case UPDATE: updateEntities.add(entity);
                        break;
                        default: throw new IllegalStateException();
                    }
                } else if (action instanceof EntityDeleteAction) {
                    deleteEntities.add(entity);
                }
            }
            entityTableAccessor.delete(deleteEntities);
            entityTableAccessor.insert(insertEntities);
            entityTableAccessor.update(updateEntities);


        }


    }
}
