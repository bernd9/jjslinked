package one.xis.sql.goal;

import lombok.Data;

import java.util.List;

// TODO validate no duplicate simple name
@Data
public class Customer {
    private List<Order> orders;
    private List<Agent> agents;
    private Address address;
    private Long id;

}
