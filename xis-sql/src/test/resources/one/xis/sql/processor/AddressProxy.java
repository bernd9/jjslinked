package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

public class AddressProxy extends Address implements EntityProxy<Address, Long> {

    private final Address entity;
    private boolean stored;
    private boolean dirty;

    public AddressProxy(Address entity, boolean stored) {
        this.entity = entity;
        this.stored = stored;
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
    public Address entity() {
        return entity;
    }

    @Override
    public boolean stored() {
        return stored;
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