package one.xis.sql.processor;

import one.xis.sql.api.EntityTableAccessor;

import java.util.Collection;


public abstract class CustomerTableAccessor extends EntityTableAccessor<Customer, Long, CustomerProxy> {

    public CustomerTableAccessor() {
        super(new CustomerStatements());
    }

    @Override
    protected void insert(CustomerProxy entityProxy) {
        insertWithDbmsGeneratedKey(entityProxy);
    }

    @Override
    protected void insert(Collection<CustomerProxy> entityProxies) {
        insertWithDbmsGeneratedKeys(entityProxies);
    }

    @Override
    protected CustomerProxy toEntityProxy(Customer entity) {
        return new CustomerProxy(entity);
    }

}