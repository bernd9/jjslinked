package one.xis.sql.mysql;

import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySQLSimpleValueMapperTest_5_7_22 extends MySQLSimpleValueMapperTest {
    MySQLSimpleValueMapperTest_5_7_22() {
        super("5.7.22");
    }
}
