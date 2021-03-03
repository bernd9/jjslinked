package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private final Customer entity;
    private boolean dirty;

    public CustomerProxy(Customer entity) {
        this.entity = entity;
    }

    @Override
    public void pk(Long pk) {
        entity.setId(pk);
    }

    @Override
    public Long pk() {
        return entity.getId();
    }

    @Override
    public Customer entity() {
        return entity;
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    @Override
    public void clean() {
        dirty = false;
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public void setLastName(String value) {
        dirty = true;
        entity.setLastName(value);
    }

    @Override
    public void setFirstName(String value) {
        dirty = true;
        entity.setFirstName(value);
    }
}