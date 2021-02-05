package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.Column;
import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

@Data
@Entity
public class OrderItem {

    @Id
    private Long id;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private Double price;

    @ForeignKey(columnName = "order_id")
    private Order order;
}
