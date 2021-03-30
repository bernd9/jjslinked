package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import com.ejc.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EntityCrudHandlerSession {

    // Do not replace this by a set. Order is important here.
    private List<EntityDatabaseActions> actionsForEntityTypes = new ArrayList<>();

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
    public void addValueUpdateAction(Object o, Consumer<?> valueUpdater, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        Class<?> entityClass = o.getClass();
        entityActionsForType(entityClass, tableAccessor, functions).addEntityForFieldUpdate(o, (Consumer<Object>) valueUpdater);
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addBulkUpdateAction(Collection<?> entities, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        if (!entities.isEmpty()) {
            Class<?> entityClass = CollectionUtils.getFirstOrThrow(entities).getClass();
            entityActionsForType(entityClass, tableAccessor, functions).addBulkUpdateAction(entities);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addBulkInsertAction(Collection<?> entities, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        if (!entities.isEmpty()) {
            Class<?> entityClass = CollectionUtils.getFirstOrThrow(entities).getClass();
            entityActionsForType(entityClass, tableAccessor, functions).addBulkInsertAction(entities);
        }
    }


    @UsedInGeneratedCode
    @SuppressWarnings({"unchecked", "unused"})
    public void addBulkDeleteAction(Collection<?> entities, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        if (!entities.isEmpty()) {
            Class<?> entityClass = CollectionUtils.getFirstOrThrow(entities).getClass();
            entityActionsForType(entityClass, tableAccessor, functions).addBulkDeleteAction(entities);
        }
    }

    Optional<EntityDatabaseActions> getEntityDatabaseActions(Class<?> entityType) {
        return findExistingEntityActions(entityType);
    }

    EntityDatabaseActions createNewEntityActions(Class<?> entityType, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        EntityDatabaseActions entityDatabaseActions = new EntityDatabaseActions(entityType, (EntityTableAccessor<Object, Object>) tableAccessor, functions);
        actionsForEntityTypes.add(entityDatabaseActions);
        return entityDatabaseActions;
    }


    void executeActions() {
        actionsForEntityTypes.forEach(EntityDatabaseActions::executeActions);
        actionsForEntityTypes.clear();
    }

    private EntityDatabaseActions entityActionsForType(Class<?> entityType, EntityTableAccessor<?, ?> tableAccessor, EntityFunctions<?, ?> functions) {
        return findExistingEntityActions(entityType).orElseGet(() -> createNewEntityActions(entityType, tableAccessor, functions));
    }

    @SuppressWarnings("unchecked")
    private Optional<EntityDatabaseActions> findExistingEntityActions(Class<?> entityType) {
        return actionsForEntityTypes.stream()
                .filter(actions -> actions.matches(entityType))
                .findFirst();
    }

}
