package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OrderProxy extends Order implements EntityProxy<Order, Long> {

    private boolean dirty;
    private final boolean readOnly;

    OrderProxy(boolean readOnly) {
        this.readOnly = readOnly;
    }

    OrderProxy() {
        this(false);
    }

    @Override
    public boolean readOnly() {
        return readOnly;
    }


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
}