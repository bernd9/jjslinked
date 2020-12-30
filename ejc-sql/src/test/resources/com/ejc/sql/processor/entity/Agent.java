package com.ejc.sql.processor.entity;

import com.ejc.sql.Entity;

import java.util.Set;

@Entity("agents")
class Agent {
    private String firstName;
    private String lastName;
    private Set<Customer> agents;
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

    public Set<Customer> getAgents() {
        return agents;
    }

    public void setAgents(Set<Customer> agents) {
        this.agents = agents;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}