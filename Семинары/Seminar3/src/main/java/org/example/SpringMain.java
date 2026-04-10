package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SpringMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserService userService) {
        return args -> {
            // Добавление пользователей
            User user1 = new User();
            user1.setName("Иван");
            userService.createUser(user1);

            User user2 = new User();
            user2.setName("Ольга");
            userService.createUser(user2);

            // Вывод всех пользователей
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                System.out.println("ID: " + user.getId() + ", Name: " + user.getName());
            }
        };
    }

}
