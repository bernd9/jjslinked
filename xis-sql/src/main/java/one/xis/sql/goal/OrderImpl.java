package one.xis.sql.goal;

import lombok.Data;

@Data
public class OrderImpl extends Order{
    private final Order order;
    private Long customerId;
}
