package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private final Customer entity;
    private boolean stored;
    private boolean dirty;

    public CustomerProxy(Customer entity, boolean stored) {
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
    public Customer entity() {
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

    @Override
    public Long getId() {
        return entity.getId();
    }

    @Override
    public void setId(Long value) {
        throw new UnsupportedOperationException("primary key can not be updated");
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

    @Override
    public Address getInvoiceAddress() {
        return entity.getInvoiceAddress();
    }

    @Override
    public void setInvoiceAddress(Address value) {
        dirty = true;
        if (value == null || value instanceof EntityProxy) {
            entity.setInvoiceAddress(value);
        } else {
            entity.setInvoiceAddress(new AddressProxy(value, false));
        }
    }
}