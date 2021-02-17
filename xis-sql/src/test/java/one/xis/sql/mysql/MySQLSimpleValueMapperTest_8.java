package one.xis.sql.mysql;

import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MySQLSimpleValueMapperTest_8 extends MySQLSimpleValueMapperTest {
    MySQLSimpleValueMapperTest_8() {
        super("8");
    }
}
