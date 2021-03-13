package one.xis.sql.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import one.xis.sql.api.EntityProxy;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.PreparedEntityStatement;
import one.xis.sql.api.action.EntityActions;
import one.xis.sql.api.action.EntityInsertAction;
import one.xis.sql.api.action.EntityUpdateAction;

public class CustomerComparer  {

    private final EntityActions entityActions;

    CustomerComparer() {
        entityActions = new EntityActions();
    }

}