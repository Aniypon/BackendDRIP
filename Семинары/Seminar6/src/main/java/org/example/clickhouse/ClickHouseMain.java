package org.example.clickhouse;

import org.example.jpa.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@ComponentScan(basePackages = "org.example.clickhouse")
@Configuration
public class ClickHouseMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("org.example.clickhouse");
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

//        insertUser(jdbcTemplate, new User(5L, "John Doe", "Software Engineer"));
//        insertUser(jdbcTemplate, new User(5L, "John Doe", "Software Engineer"));
//        multipleBatchInserts(jdbcTemplate, generateUsers());
        contingency(jdbcTemplate);

//        printUsers(jdbcTemplate);
    }

    static void printUsers(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.query(
            "SELECT * FROM users",
            (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name"), rs.getString("about"))
        ).forEach(System.out::println);
    }

    static void contingency(JdbcTemplate jdbcTemplate) {
        System.out.printf("Contingency: %s", jdbcTemplate.queryForObject("SELECT contingency(name, about) FROM users", Double.class));
    }

    static void insertUser(JdbcTemplate jdbcTemplate, User user) {
        jdbcTemplate.update("INSERT INTO users (id, name, about) VALUES (?, ?, ?)", user.getId(), user.getName(), user.getAbout());
    }

    static void multipleBatchInserts(JdbcTemplate jdbcTemplate, List<User> users) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO users (id, name, about) VALUES (?, ?, ?)",
            users,
            1000,
            (ps, user) -> {
                ps.setLong(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getAbout());
            }
        );
    }

    static List<User> generateUsers() {
        List<User> users = new ArrayList<>();
        for (long i = 0; i < 100_000; i++) {
            users.add(new User(i, "John Doe" + i, "Software Engineer"));
        }
        return users;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        dataSource.setUrl("jdbc:clickhouse://localhost:8123/mydatabase?compress=false");
        dataSource.setUsername("admin");
        dataSource.setPassword("password");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
