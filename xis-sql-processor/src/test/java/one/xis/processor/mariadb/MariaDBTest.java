package one.xis.processor.mariadb;

import one.xis.processor.DBTest;
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
