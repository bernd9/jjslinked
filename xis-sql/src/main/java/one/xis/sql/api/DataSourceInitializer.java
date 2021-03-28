package one.xis.sql.api;

import com.ejc.Bean;
import com.ejc.Configuration;
import com.ejc.Value;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;


@Configuration
class DataSourceInitializer {

    @Value("jdbc.url")
    private String jdbcUrl;

    @Value("jdbc.user")
    private String jdbcUser;

    @Value("jdbc.password")
    private String jdbUrl;

    /*
        url: jdbc:h2:mem:test
    user:
    password:
    cachePrepStmts: true
    prepStmtCacheSize: 250
    prepStmtCacheSqlLimit: 2048
     */
    @Bean
    DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test");
        config.setUsername("user");
        config.setPassword("password");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

}
