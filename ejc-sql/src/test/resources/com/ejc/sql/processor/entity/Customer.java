package com.ejc.sql.processor.entity;

import com.ejc.sql.Entity;
import com.ejc.sql.ForeignKeyRef;

import java.util.Set;

@Entity("customers")
class Customer {
    private String firstName;
    private String lastName;
    private Set<Agent> agents;

    @ForeignKeyRef(table = "customers", column = "address_id")
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

    public Set<Agent> getAgents() {
        return agents;
    }

    public void setAgents(Set<Agent> agents) {
        this.agents = agents;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}