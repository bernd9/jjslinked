package one.xis.sql.processor;

import one.xis.sql.*;

import java.util.List;

@Entity
class Customer {

    @Id
    private Long id;


    private String firstName;
    private String lastName;

    @ForeignKey(columnName = "invoice_address_id")
    private Address invoiceAddress;

    @Referenced(externalColumnName = "customer_id")
    private List<Order> orders;

    @CrossTable(tableName = "customer_agent")
    private List<Agent> agents;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(Address invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }
}