package one.xis.sql.goal;

import lombok.Data;
import one.xis.sql.Entity;
import one.xis.sql.Id;
import one.xis.sql.Unique;

@Data
@Unique(fields = "name")
public class City {

    @Id
    private String country;

    @Id
    private String postal;

    private String name;
}
