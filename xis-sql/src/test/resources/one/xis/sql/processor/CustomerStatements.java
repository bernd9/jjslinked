package one.xis.sql.processor;

import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.JdbcStatement;

public class CustomerStatements implements EntityStatements<Customer, Long> {
    @Override
    public String getInsertSql() {
        return "INSERT INTO customer (id,first_name,invoice_address_id,last_name) VALUES (?,?,?,?)";
    }

    @Override
    public String getSelectByIdSql() {
        return "SELECT id,first_name,invoice_address_id,last_name FROM customer WHERE id=?";
    }

    @Override
    public String getUpdateSql() {
        return "UPDATE customer SET first_name=?,invoice_address_id=?,last_name=? WHERE id=?";
    }

    @Override
    public String getDeleteSql() {
        return "DELETE FROM customer WHERE id=?";
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT id,first_name,invoice_address_id,last_name FROM customer";
    }

    @Override
    public String getDeleteAllSql() {
        return "DELETE FROM customer";
    }

    @Override
    public String getUpdateColumnValuesToNullByPkSql(String columnName) {
        return String.format("UPDATE customer SET %s=NULL WHERE id=?", columnName);
    }

    @Override
    public String getSelectByColumnValueSql(String columnName) {
        return String.format("SELECT id,first_name,invoice_address_id,last_name FROM customer WHERE %s=?", columnName);
    }

    @Override
    public void setInsertSqlParameters(JdbcStatement st, Customer entity) {
        st.set(1, entity.getId());
        st.set(2, entity.getFirstName());
        st.set(3, AddressUtil.getPk(entity.getInvoiceAddress()));
        st.set(4, entity.getLastName());
    }

    @Override
    public void setUpdateSqlParameters(JdbcStatement st, Customer entity) {
        st.set(1, entity.getFirstName());
        st.set(2, AddressUtil.getPk(entity.getInvoiceAddress()));
        st.set(3, entity.getLastName());
        st.set(4, entity.getId());
    }

}