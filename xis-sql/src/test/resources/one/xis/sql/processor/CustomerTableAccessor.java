package one.xis.sql.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import one.xis.sql.api.EntityTableAccessor;
import one.xis.sql.api.PreparedEntityStatement;

public class CustomerTableAccessor extends EntityTableAccessor<Customer, Long, CustomerProxy> {

    public CustomerTableAccessor() {
        super(new CustomerStatements(), Long.class);
    }

    @Override
    protected void insert(Customer entity) {
        insertWithDbmsGeneratedKey(entity);
    }

    @Override
    protected void insert(Collection<Customer> entities) {
        insertWithDbmsGeneratedKeys(entities);
    }

    @Override
    protected CustomerProxy toEntityProxy(ResultSet rs) throws SQLException {
        return new CustomerResultSet(rs).getEntityProxy();
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
    protected void setPk(PreparedEntityStatement st, int index, Long pk) {
        st.set(index, pk);
    }

    @Override
    protected Long generateKey() {
        throw new AbstractMethodError();
    }

}