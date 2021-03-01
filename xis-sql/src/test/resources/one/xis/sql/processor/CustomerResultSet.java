package one.xis.sql.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import one.xis.sql.api.EntityResultSet;

public class CustomerResultSet extends EntityResultSet<Customer> {

    CustomerResultSet(ResultSet resultSet) {
        super(resultSet);
    }

    @Override
    public Customer getEntity() throws SQLException {
        Customer entity = new Customer();
        entity.setFirstName(getString("first_name"));
        entity.setId(getLong("id"));
        entity.setLastName(getString("last_name"));
        return entity;
    }

}