package one.xis.sql.api.action;

import one.xis.sql.api.EntityTableAccessor;

public interface EntityAction<E, EID> {

    E getEntity();

    Class<E> getEntityClass();

    EntityTableAccessor<E,EID> getEntityTableAccessor();
}
