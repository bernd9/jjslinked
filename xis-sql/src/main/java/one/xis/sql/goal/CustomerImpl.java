package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.api.EntityLoaders;
import one.xis.sql.api.EntityProxy;

import javax.annotation.processing.Generated;
import java.util.List;

@Generated("xis-sql")
@Data
public class CustomerImpl extends Customer implements EntityProxy {
    private Long addressId;

    private final Customer entity;
    private final boolean orphanDelete = true;

    @Override
    public Object getPk() {
        return entity.getId();
    }

    @Override
    public Address getAddress() {
        Address value = entity.getAddress();
        if (value == null) {
            if (addressId == null) {
                return null;
            }
            value = EntityLoaders.loaderForType(Address.class).findByColumnValue("id", addressId);
            entity.setAddress(value);
        }
        return value;
    }

    @Override
    public void setAddress(Address address) {
        addressId = address.getId();
        entity.setAddress(address);
    }

    @Override
    public List<Order> getOrders() {
        List<Order> value = entity.getOrders();
        if (value == null) {
            value = EntityLoaders.loaderForType(Order.class).findAllByColumnValue("customer_id", getId(), List.class);
            entity.setOrders(value);
        }
        return entity.getOrders();
    }

    @Override
    public void setOrders(List<Order> orders) {

    }
}
