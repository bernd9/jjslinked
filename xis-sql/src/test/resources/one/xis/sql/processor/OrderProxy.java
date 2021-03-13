package one.xis.sql.processor;

import one.xis.sql.api.EntityProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OrderProxy extends Order implements EntityProxy<Order, Long> {

    private boolean dirty;
    private Map<String, Supplier<?>> suppliers = new HashMap<>();


    public static final Order NULL = new Order();

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
}