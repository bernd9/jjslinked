package one.xis.sql.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.JdbcStatement;

public class CustomerTableAccessor extends EntityTableAccessor<Customer, Long> {
    private static CustomerTableAccessor instance = new CustomerTableAccessor();

    public CustomerTableAccessor() {
        super(new CustomerStatements(), new one.xis.sql.processor.CustomerFunctions(), Customer.class, Long.class);
    }

    public static CustomerTableAccessor getInstance() {
        return instance;
    }

    @Override
    public void insert(Customer entity) {
        insertWithDbmsGeneratedKey(entity);
    }

    @Override
    public void insert(Collection<Customer> entities) {
        insertWithDbmsGeneratedKeys(entities);
    }

    @Override
    protected Long getPk(Customer entity) {
        return CustomerUtil.getPk(entity);
    }

    @Override
    protected Long getPk(ResultSet rs, int index) throws SQLException {
        return new CustomerResultSet(rs).get_Long(index);
    }

    @Override
    protected void setPk(Customer entity, Long pk) {
        CustomerUtil.setPk(entity, pk);
    }

    @Override
    protected void setPk(JdbcStatement st, int index, Long pk) {
        st.set(index, pk);
    }

    @Override
    protected Long generateKey() {
        throw new AbstractMethodError();
    }

    Optional<Customer> getByAddressId(Long key) {
        return this.getByColumnValue(key, "invoice_address_id");
    }

    <C extends Collection> C getAllByAddressId(Long key, Class<C> collectionType) {
        return (C) getAllByColumnValue(key, "invoice_address_id", collectionType);
    }
}