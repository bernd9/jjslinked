package one.xis.sql.mariadb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;

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
