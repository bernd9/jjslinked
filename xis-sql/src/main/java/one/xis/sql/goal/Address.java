package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

@Data
@Entity
public class Address {

    @Id
    private Long id;

    @ForeignKey(columnName = "city_id") // TODO : 2 columns
    private City city;
}
