package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private boolean dirty;
    private Map<String, Supplier<?>> suppliers = new HashMap<>();

    @Override
    public void pk(Long pk) {
        super.setId(pk);
    }

    @Override
    public Long pk() {
        return super.getId();
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    @Override
    public Map<String, Supplier<?>> suppliers() {
        return suppliers;
    }

    @Override
    public void setId(Long value) {
        throw new UnsupportedOperationException("primary key is immutable");
    }

    @Override
    public void setLastName(String value) {
        dirty = true;
        super.setLastName(value);
    }

    @Override
    public void setFirstName(String value) {
        dirty = true;
        super.setFirstName(value);
    }

    @Override
    public void setInvoiceAddress(Address value) {
        dirty = true;
        super.setInvoiceAddress(value);
    }

    @Override
    public Address getInvoiceAddress() {
        Address value = super.getInvoiceAddress();
        if (value == null) {
            value = load("invoiceAddress");
            super.setInvoiceAddress(value);
        }
        return value;
    }

}