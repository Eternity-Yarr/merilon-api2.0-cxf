package org.yarr.merlionapi2;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.yarr.merlionapi2.service.ConfigService;

import javax.sql.DataSource;

@Configuration
@ComponentScan("org.yarr")
public class ApplicationConfiguration
{
    @Autowired
    private ConfigService configService;

    @Bean
    public DataSource dataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(configService.mysqlUri());
        ds.setUser(configService.mysqlUser());
        ds.setPassword(configService.mysqlPassword());
        return ds;
    }
}
