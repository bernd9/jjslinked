package one.xis.processor.mariadb;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;


@Disabled
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MariaDBSimpleValueMapperTest_10 extends MariaDBSimpleValueMapperTest {
    MariaDBSimpleValueMapperTest_10() {
        super("10");
    }

    @Test
    void test() throws SQLException {
        runTest();
    }

    @BeforeAll
    void init() throws Exception {
        doInit();
    }

    @AfterAll
    void destroy() throws SQLException {
        doDestroy();
    }

}
