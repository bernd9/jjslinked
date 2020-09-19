package com.ejc.sql;

import com.ejc.Bean;
import com.ejc.Configuration;
import com.ejc.Init;
import com.ejc.Value;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Collections;
import java.util.Set;

import static com.ejc.sql.DatabaseVendor.MARIADB;
import static com.ejc.sql.DatabaseVendor.MYSQL;


@Configuration
public class DataSourceConfiguration {

    @Value(key = "jdbc.username")
    private String user;

    @Value(key = "jdbc.password")
    private String password;

    @Value(key = "jdbc.url")
    private String url;

    // TODO load automatically ?
    private Driver driver;

    @Value(key = "jdbc.driver")
    private String driverName;

    @Init
    void lookupDriver() {
        // TODO
    }

    @Bean
    DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(url);
        return dataSource;
    }

    private Set<DatabaseVendor> vendorByUrl() {
        if (url.startsWith("jdbc:mysql")) {
            return Set.of(MYSQL, MARIADB);
        }
        return Collections.emptySet();
    }

}
