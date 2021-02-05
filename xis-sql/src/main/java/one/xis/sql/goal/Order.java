package one.xis.sql.goal;


import lombok.Data;
import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

import java.util.List;

@Data
@Entity
public class Order {

    @Id
    private Long id;

    private List<OrderItem> orderItems;

    @ForeignKey(columnName = "customer_id")
    private Customer customer;

}
