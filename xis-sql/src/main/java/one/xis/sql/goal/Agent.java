package one.xis.sql.goal;

import lombok.Data;

import java.util.List;

@Data
public class Agent {
    private List<Customer> customers;
    private Long id;
}
