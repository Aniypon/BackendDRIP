package org.example;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Запросы к таблице app_data. Работает с любым DataSource (Hikari в проде, H2 в тестах).
 */
public class AppDataRepository {

    private final DataSource dataSource;

    public AppDataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long count() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM app_data")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to count app_data", e);
        }
    }

    public String firstValue() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT value FROM app_data ORDER BY id LIMIT 1")) {
            return rs.next() ? rs.getString(1) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read app_data", e);
        }
    }
}
