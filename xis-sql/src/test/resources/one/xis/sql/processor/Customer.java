package one.xis.sql.processor;

import one.xis.sql.Entity;
import one.xis.sql.Id;

@Entity
class Customer {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}