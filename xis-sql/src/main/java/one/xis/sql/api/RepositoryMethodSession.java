package one.xis.sql.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.action.EntityAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class RepositoryMethodSession {

    private List<EntityActions> actionsForEntityTypes = new ArrayList<>();

    static RepositoryMethodSession getInstance() {

        return null;
    }


    void addAction(EntityAction<?,?> action) {

    }

    private EntityAction<?,?> entityActionFor(Class<?> entityType) {
        return null;
    }

    private Optional<EntityActions> findExisting(Class<?> entityType) {
        return actionsForEntityTypes.stream()
                .filter(actions -> actions.matches(entityType))
                .findFirst();
    }


    private EntityActions createNew(Class<?> entityType) {
        EntityActions entityActions = new EntityActions(entityType);
        actionsForEntityTypes.add(entityActions);
        return entityActions;
    }


    @Data
    class EntityActions {

        private final Class<?> entityType;
        private final List<ActionItem> items = new ArrayList<>();

        ActionItem createOrAppendItem(EntityAction action) {
            int hashCode = System.identityHashCode(action.getEntity());
            return null;
        }

        boolean matches(Class<?> type) {
            return entityType.equals(type);
        }

    }

    @Data
    @RequiredArgsConstructor
    class ActionItem {
        private final int hashCode;
    }
}
