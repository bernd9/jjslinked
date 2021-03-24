package one.xis.sql.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import one.xis.sql.api.EntityProxy;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private boolean dirty;

    private Map<String, Supplier> suppliers;

    CustomerProxy() {
        suppliers = new HashMap<>();
    }

    @Override
    public Map<String, Supplier<?>> suppliers() {
        return suppliers();
    }

    @Override
    public void pk(Long pk) {
        if (pk() != null) throw new UnsupportedOperationException("primary key is immutable");
        CustomerUtil.setPk(this, pk);
    }

    @Override
    public Long pk() {
        return CustomerUtil.getPk(this);
    }

    @Override
    public boolean dirty() {
        return dirty;
    }

    @Override
    public void doSetClean() {
        dirty = false;
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
    public void setInvoiceAddress(Address value) {
        dirty = true;
        super.setInvoiceAddress(value);
    }

    @Override
    public void setFirstName(String value) {
        dirty = true;
        super.setFirstName(value);
    }

}