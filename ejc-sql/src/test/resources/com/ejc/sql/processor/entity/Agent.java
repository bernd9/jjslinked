package com.ejc.sql.processor.entity;

import com.ejc.sql.Entity;
import com.ejc.sql.ForeignKeyRef;

import java.util.Set;

@Entity("agents")
class Agent {
    private String firstName;
    private String lastName;

    @ForeignKeyRef(table = "customers", column = "agent_id")
    private Set<Customer> customers;
    private Address address;

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

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}