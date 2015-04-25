package org.yarr.merlionapi2.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.service.ConfigService;

import javax.activation.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class Database
{
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private ComboPooledDataSource dataSource = new ComboPooledDataSource();

    @Autowired
    public Database(ConfigService configService) {
        try
        {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            log.error("Error during loading mysql driver", e);
        }
        dataSource.setJdbcUrl(configService.mysqlUri());
        dataSource.setUser(configService.mysqlUser());
        dataSource.setPassword(configService.mysqlPassword());
    }

    public Connection c() throws SQLException {
        return dataSource.getConnection();
    }
}
