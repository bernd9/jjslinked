package one.xis.sql.processor;

import java.util.Collection;
import one.xis.sql.api.EntityTableAccessor;


public abstract class CustomerTableAccessor extends EntityTableAccessor<Customer, Long, CustomerProxy> {

    @Override
    protected void insert(CustomerProxy entityProxy) {
        insertWithDbmsGeneratedKey(entityProxy);
    }

    @Override
    protected void insert(Collection<CustomerProxy> entityProxies) {
        insertWithDbmsGeneratedKeys(entityProxies);
    }

    @Override
    protected CustomerProxy toEntityProxy(Customer entity, boolean stored) {
        return new CustomerProxy(entity, stored);
    }

}