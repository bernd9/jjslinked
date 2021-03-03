package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private final Customer entity;
    private boolean dirty;

    public CustomerProxy(Customer entity) {
        this.entity = entity;
    }

    public void pk(Long pk) {
        entity.setId(pk);
    }

    public Long pk() {
        return entity.getId();
    }

    public Customer entity() {
        return entity;
    }

    public boolean dirty() {
        return dirty;
    }

    public void clean() {
        dirty = false;
    }

}