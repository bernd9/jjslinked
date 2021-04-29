package one.xis.sql.processor;

import java.util.List;
import one.xis.sql.api.EntityProxy;
import one.xis.sql.api.FieldValueLoader;

public class CustomerProxy extends Customer implements EntityProxy<Customer, Long> {

    private boolean dirty;
    private final boolean readOnly;

    private final FieldValueLoader<Long, List<Order>> ordersLoader = new FieldValueLoader<Long, List<Order>>(key -> OrderTableAccessor.getInstance().getAllByCustomerId(key, java.util.List.class));
    private final FieldValueLoader<Long, Address> invoiceAddressLoader = new FieldValueLoader<Long, Address>(key -> AddressTableAccessor.getInstance().findById(key).orElse(null));

    CustomerProxy(boolean readOnly) {
        this.readOnly = readOnly;
    }

    CustomerProxy() {
        this(false);
    }

    @Override
    public boolean readOnly() {
        return readOnly;
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
    public void setLastName(String value) {
        dirty = true;
        super.setLastName(value);
    }

}