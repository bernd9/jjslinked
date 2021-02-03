package one.xis.sql.goal;


import one.xis.sql.Entity;
import one.xis.sql.Id;

import java.util.List;

@Entity
public class Order {

    @Id
    private Long id;

    private List<OrderItem> orderItems;

}
