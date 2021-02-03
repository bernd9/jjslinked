package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.CrossTable;
import one.xis.sql.Entity;
import one.xis.sql.Id;

import java.util.List;

@Data
@Entity
public class Agent {
    @Id
    private Long id;

    @CrossTable(tableName = "customers_agents")
    private List<Customer> customers;
}
