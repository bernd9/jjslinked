package one.xis.sql.processor;

import one.xis.sql.api.RepositoryImpl;

public class CustomerRepositoryImpl extends RepositoryImpl<Customer, Long> implements CustomerRepository {

    public CustomerRepositoryImpl() {
        super(new CustomerCrudHandler());
    }


}