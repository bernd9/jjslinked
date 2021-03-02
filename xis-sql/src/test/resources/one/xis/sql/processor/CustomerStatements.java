package one.xis.sql.processor;

import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.PreparedEntityStatement;

public class CustomerStatements implements EntityStatements<Customer, Long> {

    @Override
    public String getInsertSql() {
        return "INSERT INTO `customer` (`id`,`first_name`,`last_name`) VALUES (?,?,?)";
    }

    @Override
    public String getSelectByIdSql() {
        return "SELECT `id`,`first_name`,`last_name` FROM `customer` WHERE `id`=?";
    }

    @Override
    public String getUpdateSql() {
        return "UPDATE `customer` SET `first_name`=?,`last_name`=? WHERE `id`=?";
    }

    @Override
    public String getDeleteSql() {
        return "DELETE FROM `customer` WHERE `id`=?";
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT `id`,`first_name`,`last_name` FROM `customer`";
    }

    @Override
    public String getDeleteAllSql() {
        return "DELETE FROM `customer`";
    }

    @Override
    public void setInsertSqlParameters(PreparedEntityStatement st, Customer entity) {
        st.set(1, entity.getFirstName());
        st.set(2, entity.getId());
        st.set(3, pk(entity.getInvoiceAddress(), java.lang.Long.class));
        st.set(4, entity.getLastName());
    }

    @Override
    public void setUpdateSqlParameters(PreparedEntityStatement st, Customer entity) {
        st.set(1, entity.getFirstName());
        st.set(2, entity.getId());
        st.set(3, pk(entity.getInvoiceAddress(), java.lang.Long.class));
        st.set(4, entity.getLastName());
    }


}