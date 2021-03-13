package one.xis.sql.processor;

import one.xis.sql.api.EntityResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerResultSet extends EntityResultSet<Customer, Long, CustomerProxy> {

    CustomerResultSet(ResultSet resultSet) {
        super(resultSet);
    }

    @Override
    public CustomerProxy getEntity() throws SQLException {
        CustomerProxy entity = new CustomerProxy();
        entity.setFirstName(get_String("first_name"));
        entity.setId(get_Long("id"));
        entity.setLastName(get_String("last_name"));
        return entity;
    }

}