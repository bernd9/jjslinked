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
        entity.setFirstName(get_String("first_name"));
        entity.setId(get_Long("id"));
        entity.setLastName(get_String("last_name"));
        return new CustomerProxy(entity, true);
    }

}