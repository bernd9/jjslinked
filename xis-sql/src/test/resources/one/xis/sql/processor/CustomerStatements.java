package one.xis.sql.processor;

import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.PreparedEntityStatement;

public class CustomerStatements implements EntityStatements<Customer, Long> {
    @Override
    public String getInsertSql() {
        return "INSERT INTO `customer` (`id`,`address_id`,`first_name`,`last_name`) VALUES (?,?,?,?)";
    }

    @Override
    public String getSelectByIdSql() {
        return "SELECT `id`,`address_id`,`first_name`,`last_name` FROM `customer` WHERE `id`=?";
    }

    @Override
    public String getUpdateSql() {
        return "UPDATE `customer` SET `address_id`=?,`first_name`=?,`last_name`=? WHERE `id`=?";
    }

    @Override
    public String getDeleteSql() {
        return "DELETE FROM `customer` WHERE `id`=?";
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT `id`,`address_id`,`first_name`,`last_name` FROM `customer`";
    }

    @Override
    public String getDeleteAllSql() {
        return "DELETE FROM `customer`";
    }

    @Override
    public String getUpdateColumnValuesToNullByPkSql(String columnName) {
        return String.format("UPDATE `customer` SET `%s`=NULL WHERE `id`=?", columnName);
    }

    @Override
    public String getPksByColumnValueSql(String columnName) {
        return String.format("SELECT `id` FROM `customer` WHERE `%s`=?", columnName);
    }

    @Override
    public void setInsertSqlParameters(PreparedEntityStatement st, Customer entity) {
        st.set(1, entity.getId());
        st.set(2, AddressUtil.getPk(entity.getInvoiceAddress()));
        st.set(3, entity.getFirstName());
        st.set(4, entity.getLastName());
    }

    @Override
    public void setUpdateSqlParameters(PreparedEntityStatement st, Customer entity) {
        st.set(1, AddressUtil.getPk(entity.getInvoiceAddress()));
        st.set(2, entity.getFirstName());
        st.set(3, entity.getLastName());
        st.set(4, entity.getId());
    }

}