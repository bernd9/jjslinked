package one.xis.sql.processor;

import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.PreparedEntityStatement;

public class AddressStatements implements EntityStatements<Address, Long> {
    @Override
    public String getInsertSql() {
        return "INSERT INTO `address` (`id`,`country`,`postal`,`street`) VALUES (?,?,?,?)";
    }

    @Override
    public String getSelectByIdSql() {
        return "SELECT `id`,`country`,`postal`,`street` FROM `address` WHERE `id`=?";
    }

    @Override
    public String getUpdateSql() {
        return "UPDATE `address` SET `country`=?,`postal`=?,`street`=? WHERE `id`=?";
    }

    @Override
    public String getDeleteSql() {
        return "DELETE FROM `address` WHERE `id`=?";
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT `id`,`country`,`postal`,`street` FROM `address`";
    }

    @Override
    public String getDeleteAllSql() {
        return "DELETE FROM `address`";
    }

    @Override
    public String getUpdateColumnValuesToNullByPkSql(String columnName) {
        return String.format("UPDATE `address` SET `%s`=NULL  WHERE `id`=?", columnName);
    }

    @Override
    public String getPksByColumnValueSql(String columnName) {
        return String.format("SELECT `id` FROM `address` WHERE `%s`=?", columnName);
    }

    @Override
    public void setInsertSqlParameters(PreparedEntityStatement st, Address entity) {
        st.set(1, entity.getId());
        st.set(2, entity.getCountry());
        st.set(3, entity.getPostal());
        st.set(4, entity.getStreet());
    }

    @Override
    public void setUpdateSqlParameters(PreparedEntityStatement st, Address entity) {
        st.set(1, entity.getCountry());
        st.set(2, entity.getPostal());
        st.set(3, entity.getStreet());
        st.set(4, entity.getId());
    }

}