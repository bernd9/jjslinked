package one.xis.sql.api;

import lombok.Data;
import one.xis.sql.api.action.EntityAction;
import one.xis.sql.api.action.EntityDeleteAction;
import one.xis.sql.api.action.EntitySaveAction;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class CrudHandlerSession {

    // Do not replace this by a set. Order is important here.
    private List<EntityActions<?>> actionsForEntityTypes = new ArrayList<>();

    static CrudHandlerSession getInstance() {
        return Session.getInstance().getCrudHandlerSession();
    }

    @SuppressWarnings("unchecked")
    <E> boolean hasSaveAction(E o) {
        Class<E> entityClass = (Class<E>) o.getClass();
        return findExistingEntityActions(entityClass).map(a -> a.hasSaveAction(o)).orElse(false);
    }

    @SuppressWarnings("unchecked")
    <E> boolean hasDeleteAction(E o) {
        Class<E> entityClass = (Class<E>) o.getClass();
        return findExistingEntityActions(entityClass).map(a -> a.hasDeleteAction(o)).orElse(false);
    }

    @SuppressWarnings("unchecked")
    <E, EID> void addSaveAction(E o, EntityTableAccessor<E,EID> tableAccessor) {
        Class<E> entityClass = (Class<E>) o.getClass();
        entityActionsForType(entityClass).addSaveAction(o, tableAccessor);
    }

    @SuppressWarnings("unchecked")
    <E, EID> void addDeleteAction(E o, EntityTableAccessor<E,EID> tableAccessor) {
        Class<E> entityClass = (Class<E>) o.getClass();
        entityActionsForType(entityClass).addDeleteAction(o, tableAccessor);
    }

    @SuppressWarnings("unchecked")
    <E, EID> void addValueUpdateAction(E o, Consumer<E> valueUpdater, EntityTableAccessor<E,EID> tableAccessor) {
        Class<E> entityClass = (Class<E>) o.getClass();
        entityActionsForType(entityClass).addValueUpdateAction(o, valueUpdater, tableAccessor);
    }

    List<EntityActions<Object>> flushActions() {
        try {
            return actionsForEntityTypes.stream()
                    .map(EntityActions::getEntityActions)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .map(a -> (EntityActions<Object>) a)
                    .collect(Collectors.toUnmodifiableList());
        } finally {
            clear();
        }
    }

    void clear() {
        actionsForEntityTypes.clear();
    }

    private <E> EntityActions<E> entityActionsForType(Class<E> entityType) {
        return findExistingEntityActions(entityType).orElseGet(() -> createNewEntityActions(entityType));
    }

    @SuppressWarnings("unchecked")
    private <E> Optional<EntityActions<E>> findExistingEntityActions(Class<E> entityType) {
        return actionsForEntityTypes.stream()
                .filter(actions -> actions.matches(entityType))
                .map(a -> (EntityActions<E>) a)
                .findFirst();
    }

    private <E> EntityActions<E> createNewEntityActions(Class<E> entityType) {
        EntityActions<E> entityActions = new EntityActions<>(entityType);
        actionsForEntityTypes.add(entityActions);
        return entityActions;
    }


    @Data
    private static class EntityActions<E> {

        private final Class<E> entityType;
        private Map<Integer, EntityAction<E>> entityActions = new HashMap<>();

        boolean hasSaveAction(E o) {
            return Optional.ofNullable(entityActions.get(System.identityHashCode(o)))
                    .map(EntitySaveAction.class::isInstance)
                    .orElse(false);
        }

        boolean hasDeleteAction(E o) {
            return Optional.ofNullable(entityActions.get(System.identityHashCode(o)))
                    .map(EntityDeleteAction.class::isInstance)
                    .orElse(false);
        }

        boolean matches(Class<?> type) {
            return entityType.equals(type);
        }

        void addSaveAction(E o, EntityTableAccessor<E, ?> tableAccessor) {
            // replace previous action
            entityActions.put(System.identityHashCode(o), new EntitySaveAction<>(o, tableAccessor));
        }

        void addDeleteAction(E o, EntityTableAccessor<E, ?> tableAccessor) {
            // replace previous action
            entityActions.put(System.identityHashCode(o), new EntityDeleteAction<>(o, tableAccessor));
        }

        void addValueUpdateAction(E o, Consumer<E> valueUpdater, EntityTableAccessor<E, ?> tableAccessor) {
            int hashCode = System.identityHashCode(o);
            EntityAction<E> action = entityActions.get(hashCode);
            if (action == null) {
                valueUpdater.accept(o);
                entityActions.put(hashCode, new EntitySaveAction<>(o, tableAccessor));
            } else if (action instanceof EntitySaveAction) {
                // combine 2 actions:
                valueUpdater.accept(action.getEntity());
            } else if (action instanceof EntityDeleteAction){
                // updating a reference to an object, that was deleted before.
                throw new IllegalStateException("you are trying to update deleted entity " + o);
            } else {
                throw new IllegalStateException("unknown action: " + action);
            }
        }
    }

}
