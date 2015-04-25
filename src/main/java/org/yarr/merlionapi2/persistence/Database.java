package org.yarr.merlionapi2.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;

public class Database
{
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    ComboPooledDataSource dataSource = new ComboPooledDataSource();
    public Database() {
        try
        {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            log.error("Error during loading mysql driver", e);
        }
 /*       dataSource.setJdbcUrl(Main.c().DB_uri);
        dataSource.setUser(Main.c().DB_user);
        dataSource.setPassword(Main.c().DB_pass);*/
    }
}
