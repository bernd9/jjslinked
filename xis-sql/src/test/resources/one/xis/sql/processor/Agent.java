package one.xis.sql.processor;

import one.xis.sql.CrossTable;
import one.xis.sql.Entity;
import one.xis.sql.Id;

import java.util.List;


@Entity
class Agent {

    @Id
    private Long id;
    private String name;

    @CrossTable(tableName = "customers_agents")
    private List<Customer> customers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}