package one.xis.sql.api;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Data
class EntityDatabaseActions {

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
                if (!updateEntities.containsKey(hashCode)) {
                    throw new IllegalStateException();
                }
                if (entityProxy.readOnly()) {
                    throw new IllegalStateException("you are trying to update a read only-object: " +o
                            +". @Service or @Transactional-Annotation will fix this issue.");
                }
                updateEntities.put(hashCode, entityProxy);
                entityProxy.doSetClean();
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

    void addBulkUpdateAction(Stream<?> entities) {
        entities.forEach(e -> updateEntities.put(System.identityHashCode(e), e));
    }

    void addBulkInsertAction(Stream<?> entities) {
        entities.forEach(e -> insertEntities.put(System.identityHashCode(e), e));
    }

    void addBulkDeleteAction(Stream<?> entities) {
        entities.forEach(e -> deleteEntities.put(System.identityHashCode(e), e));
    }

    @SuppressWarnings("unchecked")
    void executeActions() {
        Session.required();
        Session session = Session.getInstance();

        entityTableAccessor.delete(deleteEntities.values());
        deleteEntities.values().forEach(session::unregister);

        entityTableAccessor.insert(insertEntities.values());
        UnaryOperator<Object> cloneFunction = o -> (UnaryOperator<Object>) functions;
        insertEntities.values().forEach(entity -> session.register(entity, cloneFunction));

        entityTableAccessor.update(updateEntities.values());
        updateEntities.values().forEach(entity -> session.register(entity, cloneFunction));
    }
}
