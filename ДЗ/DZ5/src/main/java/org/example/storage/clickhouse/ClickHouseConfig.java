package org.example.storage.clickhouse;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ClickHouseConfig {

    @Bean
    public JdbcTemplate clickHouseJdbcTemplate(
            @Value("${clickhouse.url}") String url,
            @Value("${clickhouse.user}") String user,
            @Value("${clickhouse.password}") String password) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        DataSource dataSource = new ClickHouseDataSource(url, properties);
        return new JdbcTemplate(dataSource);
    }
}
