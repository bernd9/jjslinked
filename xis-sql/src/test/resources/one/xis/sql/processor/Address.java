package one.xis.sql.processor;

import one.xis.sql.Entity;
import one.xis.sql.Id;
import one.xis.sql.Referred;

@Entity
class Address {

    @Id
    private Long id;

    private String street;

    private String postal;

    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}