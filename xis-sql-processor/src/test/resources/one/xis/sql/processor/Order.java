package one.xis.sql.processor;

import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

@Entity
class Order {

    @Id
    private Long id;

    @ForeignKey(columnName = "customer_id")
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}