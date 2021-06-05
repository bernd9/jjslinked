package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AddressProxy extends Address implements EntityProxy<Address, Long> {

    private boolean dirty;
    private final boolean readOnly;

    AddressProxy(boolean readOnly) {
        this.readOnly = readOnly;
    }

    AddressProxy() {
        this(false);
    }

    @Override
    public boolean readOnly() {
        return readOnly;
    }

    public static final Address NULL = new Address();

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
    public void setCountry(String value) {
        dirty = true;
        super.setCountry(country);
    }

    @Override
    public void setPostal(String value) {
        dirty = true;
        super.setPostal(value);
    }

    @Override
    public void setStreet(String street) {
        dirty = true;
        super.setStreet(street);
    }
}