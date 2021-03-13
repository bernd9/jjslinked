package one.xis.sql.api.action;

import java.util.*;

public class EntityActions {
    private final Map<Class<?>, Collection<EntityAction>> actions = new HashMap<>();

    public void add(EntityAction action) {
        actions.computeIfAbsent(action.getEntityClass(), c -> new HashSet<>()).add(action);
    }

    public void addAll(Collection<EntityAction> actions, Class<?> entityClass) {
        this.actions.computeIfAbsent(entityClass, c -> new HashSet<>()).addAll(actions);
    }
}
