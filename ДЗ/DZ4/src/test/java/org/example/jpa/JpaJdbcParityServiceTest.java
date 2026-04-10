package org.example.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JpaJdbcParityServiceTest {

    @Autowired
    private JpaJdbcParityService service;

    @BeforeEach
    void clean() {
        service.clearUsers();
    }

    @Test
    void insertUserPersistsSingleRow() {
        service.insertUser(new User("John Doe", "Test"));
        assertEquals(1, service.countUsers());
    }

    @Test
    void insertMultipleUsersPersistsAllRows() {
        service.insertMultipleUsers(List.of(
                new User("A", "Test"),
                new User("B", "Test"),
                new User("C", "Test")
        ));
        assertEquals(3, service.countUsers());
    }

    @Test
    void clearUsersEmptiesTable() {
        service.insertUser(new User("Temp", "Test"));
        service.clearUsers();
        assertEquals(0, service.countUsers());
    }

    @Test
    void rollbackLeavesTableEmpty() {
        service.insertWithTransactionRollback(List.of(
                new User("A", "Test"),
                new User("B", "Test")
        ));
        assertEquals(0, service.countUsers());
    }
}
