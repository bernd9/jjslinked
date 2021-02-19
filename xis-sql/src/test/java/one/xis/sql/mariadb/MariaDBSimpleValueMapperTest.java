package one.xis.sql.mariadb;

import com.mysql.cj.MysqlType;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;


class MariaDBSimpleValueMapperTest extends MariaDBTest {

    private static final String TABLE_NAME = "test1";

    MariaDBSimpleValueMapperTest(String version) {
        super(version);
    }

    protected void runTest() throws SQLException {
        DatabaseMetaData metaData = getConnection().getMetaData();
        ResultSet columns = metaData.getColumns(getConnection().getCatalog(), null, TABLE_NAME, "%");
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
        StringBuilder createTable = new StringBuilder("create table ");
        // createTable.append("`");
        createTable.append(TABLE_NAME);
        // createTable.append("`");
        createTable.append(" (");

        //Iterator<MysqlType> typeIterator = Set.of(INT, BIT).iterator();

        Iterator<MysqlType> typeIterator = List.of(MysqlType.BIGINT, MysqlType.DATE)
                .iterator();


        int index = 0;
        while (typeIterator.hasNext()) {
            MysqlType mysqlType = typeIterator.next();
            // createTable.append("`");
            createTable.append("col_");
            createTable.append(index++);
            // createTable.append("`");
            createTable.append(" ");
            createTable.append(mysqlType.getName());
            if (typeIterator.hasNext()) {
                createTable.append(",");
            }
        }
        createTable.append(")");
        execute(createTable.toString());
    }

    @Override
    protected void destroy(Connection con) throws SQLException {
        dropTable();
    }

    private void dropTable() throws SQLException {
        execute("drop table " + TABLE_NAME);
    }
}