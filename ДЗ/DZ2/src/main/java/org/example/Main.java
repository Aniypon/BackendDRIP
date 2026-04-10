package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        queryDatabase(
                "DB1",
                "jdbc:postgresql://postgres-db1:5432/app_db1",
                "app_user",
                "app_password"
        );

        queryDatabase(
                "DB2",
                "jdbc:postgresql://postgres-db2:5432/app_db2",
                "app_user",
                "app_password"
        );
    }

    private static void queryDatabase(String label, String jdbcUrl, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10_000);

        try (HikariDataSource dataSource = new HikariDataSource(config);
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet countRs = statement.executeQuery("SELECT COUNT(*) FROM app_data")) {
                if (countRs.next()) {
                    System.out.printf("%s -> rows in app_data: %d%n", label, countRs.getInt(1));
                }
            }

            try (ResultSet textRs = statement.executeQuery("SELECT value FROM app_data ORDER BY id LIMIT 1")) {
                if (textRs.next()) {
                    System.out.printf("%s -> first value: %s%n", label, textRs.getString(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to query " + label, e);
        }
    }
}
