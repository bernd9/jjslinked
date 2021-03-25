package one.xis.sql.api.action;

import one.xis.sql.api.EntityTableAccessor;

public interface EntityAction<E> {

    E getEntity();

    EntityTableAccessor<E,?> getEntityTableAccessor();
}
