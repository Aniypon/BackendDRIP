package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/mydatabase";
        String username = "admin";
        String password = "password";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Подключение установлено!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM users WHERE id = " + 1);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("username");
                System.out.println("ID: " + id + ", Имя: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "SELECT * FROM users WHERE id > ?";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 2); // Устанавливаем параметр
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Имя: " + resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertQuery = "INSERT INTO users (id, username, email) VALUES (?, ?, ?)";
        PreparedStatement insertStatement = null;

        try {
            insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, 4);
            insertStatement.setString(2, "Иван");
            insertStatement.setString(3, "ivan@example.com");
            int rowsAffected = insertStatement.executeUpdate();
            System.out.println(rowsAffected + " записей добавлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.setAutoCommit(false); // Начало транзакции

            // Обновляем email пользователя с id = 1
            PreparedStatement stmt1 = connection.prepareStatement(
                "UPDATE users SET email = ? WHERE id = ?"
            );
            stmt1.setString(1, "new_email@example.com");
            stmt1.setLong(2, 1L);

            // Обновляем имя пользователя с id = 2
            PreparedStatement stmt2 = connection.prepareStatement(
                "UPDATE users SET username = ? WHERE id = ?"
            );
            stmt2.setString(1, "new_username");
            stmt2.setLong(2, 2L);

            int rows1 = stmt1.executeUpdate();
            int rows2 = stmt2.executeUpdate();

            if (rows1 > 0 && rows2 > 0) {
                connection.commit(); // Фиксируем транзакцию
                System.out.println("Транзакция успешно выполнена.");
            } else {
                connection.rollback(); // Откатываем транзакцию
                System.out.println("Транзакция отменена: не все обновления прошли успешно.");
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Откатываем при ошибке
                }
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате транзакции: " + ex.getMessage());
            }
            System.err.println("Ошибка при выполнении транзакции: " + e.getMessage());
        }

        query = "SELECT * FROM users WHERE id > ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, 2);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Имя: " + rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String callProcedure = "{call get_user_by_id(?)}";

        try (CallableStatement cstmt = connection.prepareCall(callProcedure)) {
            cstmt.setInt(1, 1);
            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Имя: " + rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10); // Максимальное количество соединений, которые могут быть открыты в пуле одновременно.
        config.setMinimumIdle(2); // Минимальное количество простаивающих (idle) соединений в пуле.
        config.setIdleTimeout(30000); // Время, в миллисекундах, в течение которого соединение может простаивать, прежде чем будет закрыто.
        config.setMaxLifetime(1800000); // Максимальное время жизни активного соединения в миллисекундах, после которого оно будет заменено.
        config.setConnectionTestQuery("SELECT 1");

        HikariDataSource dataSource = new HikariDataSource(config);
        String sql = "SELECT * FROM get_user_by_id(?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, 1);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("Имя: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Дата создания: " + rs.getTimestamp("created_at"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}