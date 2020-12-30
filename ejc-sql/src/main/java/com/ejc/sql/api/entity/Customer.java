package com.ejc.sql.api.entity;

import com.ejc.sql.CrossTableRef;
import com.ejc.sql.Entity;
import com.ejc.sql.ForeignKeyRef;
import com.ejc.sql.Id;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity("customer")
public class Customer {

    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @ForeignKeyRef(table = "customer", column = "address_id")
    private Address address;

    @CrossTableRef(table = "customer_agent_ref", entityColumn = "customer_id", attributeColumn = "agent_id")
    private Set<Agent> agents;

    @ForeignKeyRef(table = "order", column = "customer_id")
    private List<Order> order;


}
