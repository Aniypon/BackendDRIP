package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            AppDataRepository repository = new AppDataRepository(dataSource);
            System.out.printf("%s -> rows in app_data: %d%n", label, repository.count());
            System.out.printf("%s -> first value: %s%n", label, repository.firstValue());
        }
    }
}
