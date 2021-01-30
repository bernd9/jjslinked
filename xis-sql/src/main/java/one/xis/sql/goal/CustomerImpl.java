package one.xis.sql.goal;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import one.xis.sql.EntityImpl;

import javax.annotation.processing.Generated;

@Generated("xis-sql")
@Data
public class CustomerImpl extends Customer implements EntityImpl {
    private final Customer customer;
    private Long addressId;

    private final boolean orphanDelete = true;

    @Override
    public Object getPk() {
        return customer.getId();
    }
}
