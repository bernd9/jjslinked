package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

public class AddressProxy extends Address implements EntityProxy<Address, Long> {

    private final Address entity;
    private boolean dirty;

    public AddressProxy(Address entity) {
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

}