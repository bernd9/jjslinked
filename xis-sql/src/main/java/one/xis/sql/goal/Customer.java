package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.CrossTable;
import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

import java.util.List;


@Data
@Entity
public class Customer {

    @Id
    private Long id;
    // no annotation required
    private List<Order> orders;

    @CrossTable(tableName = "customers_agents")
    private List<Agent> agents;

    @ForeignKey
    private Address address;


}
