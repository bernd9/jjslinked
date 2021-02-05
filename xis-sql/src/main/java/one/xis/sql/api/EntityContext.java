package one.xis.sql.api;

public class EntityContext {
    private static final ThreadLocal<EntityContext> entityContextThreadLocal = ThreadLocal.withInitial(EntityContext::new);

    static EntityContext getInstance() {
        return entityContextThreadLocal.get();
    }

}
