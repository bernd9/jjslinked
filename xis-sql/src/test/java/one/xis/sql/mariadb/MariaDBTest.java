package one.xis.sql.mariadb;

import one.xis.sql.DBTest;
import org.testcontainers.containers.MariaDBContainer;


abstract class MariaDBTest extends DBTest<MariaDBContainer> {
    
    public MariaDBTest(String version) {
        super(version);
    }


    @Override
    protected MariaDBContainer getContainer(String version) {
        return new MariaDBContainer("mariadb:" + version);
    }
}
