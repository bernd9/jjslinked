package one.xis.sql.api;

import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class EntityResultSet<E, EID, P extends EntityProxy<E, EID>> extends ExtendedResultSet {

    public EntityResultSet(ResultSet resultSet) {
        super(resultSet);
    }

    public abstract P getEntityProxy() throws SQLException;

}
