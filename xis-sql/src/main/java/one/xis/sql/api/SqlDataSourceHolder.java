package one.xis.sql.api;

import com.ejc.Init;
import com.ejc.Singleton;
import com.ejc.Value;

import javax.sql.DataSource;


@Singleton
class SqlDataSourceHolder {

    @Value("jdbc.url")
    private String jdbcUrl;

    @Value("jdbc.user")
    private String jdbcUser;

    @Value("jdbc.password")
    private String jdbUrl;

    private DataSource dataSource;

    @Init
    void init() {

    }

    DataSource getDataSource() {
        return null;
    }

}
