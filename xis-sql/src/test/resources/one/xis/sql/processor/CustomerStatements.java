package one.xis.sql.processor;

import one.xis.sql.api.EntityStatements;

public class CustomerStatements implements EntityStatements<Customer, Long> {

    @Override
    public String getInsertSql() {
        return "INSERT INTO `customer` (`id`,`first_name`,`last_name`) VALUES (?,?,?)";
    }
    
}