package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.*;

import java.util.List;


@Data
@Entity
public class Customer {

    @Id
    private Long id;
    // no annotation required

    @Referred("customer_id")
    private List<Order> orders;

    @CrossTable(tableName = "customers_agents")
    private List<Agent> agents;

    @ForeignKey(columnName = "address_id", onDelete = ForeignKeyAction.SET_NULL_DBMS)
    private Address address;


}
