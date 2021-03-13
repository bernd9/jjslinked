package one.xis.sql.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.PreparedEntityStatement;

public class CustomerCrudRepositoryImpl  {

    private final CustomerTableAccessor customerTableAccessor;
    private final AddressTableAccessor addressTableAccessor;

    public CustomerCrudRepositoryImpl() {
        customerTableAccessor = new CustomerTableAccessor();
    }

    public void save(Customer customer) {
        customerTableAccessor.save(customer);
    }

}