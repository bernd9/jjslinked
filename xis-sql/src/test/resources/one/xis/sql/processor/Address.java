package one.xis.sql.processor;

import one.xis.sql.Entity;
import one.xis.sql.Id;
import one.xis.sql.Referred;

@Entity
class Address {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}