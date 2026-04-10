package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AppDataRepositoryTest {

    private HikariDataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:dz2test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;NON_KEYWORDS=VALUE");
        config.setUsername("sa");
        config.setPassword("");
        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS app_data");
            statement.execute("CREATE TABLE app_data (id INT PRIMARY KEY, value VARCHAR(255))");
            statement.execute("INSERT INTO app_data (id, value) VALUES (1, 'hello from db1')");
            statement.execute("INSERT INTO app_data (id, value) VALUES (2, 'second')");
        }
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void countReturnsRowNumber() {
        assertEquals(2, new AppDataRepository(dataSource).count());
    }

    @Test
    void firstValueReturnsLowestId() {
        assertEquals("hello from db1", new AppDataRepository(dataSource).firstValue());
    }

    @Test
    void emptyTableReturnsZeroAndNull() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM app_data");
        }
        AppDataRepository repository = new AppDataRepository(dataSource);
        assertEquals(0, repository.count());
        assertNull(repository.firstValue());
    }
}
