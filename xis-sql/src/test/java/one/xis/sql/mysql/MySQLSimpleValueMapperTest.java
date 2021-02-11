package one.xis.sql.mysql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MySQLSimpleValueMapperTest extends MySQLTest {


    MySQLSimpleValueMapperTest(String version) {
        super(version);
    }

    @Test
    void test() throws SQLException {
        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet columns = metaData.getColumns(getConnection().getCatalog(), null, "map_test", "%");
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            int columnType = columns.getInt("DATA_TYPE");
            System.out.printf("name: %s, type: %d", columnName, columnType);
            System.out.println();
        }
    }

    @Override
    protected void init(Connection con) throws SQLException {
        createTable();
    }

    private void createTable() throws SQLException {
        execute("create table map_test(i1 tinyint, i2 int, i3 bigint, i4 decimal)");
    }

    @Override
    protected void destroy(Connection con) throws SQLException {
        dropTable();
    }

    private void dropTable() throws SQLException {
        execute("drop table map_test");
    }
}