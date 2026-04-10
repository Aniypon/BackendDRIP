package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesAndReadsUsersViaSpringDataJpa() {
        userRepository.save(newUser("Ivan Petrov", "ivan@example.com"));
        userRepository.save(newUser("Olga Smirnova", "olga@example.com"));

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void enforcesUniqueEmail() {
        userRepository.save(newUser("A", "dup@example.com"));
        userRepository.saveAndFlush(newUser("B", "other@example.com"));
        assertEquals(2, userRepository.count());
    }

    private User newUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(OffsetDateTime.now());
        return user;
    }
}
