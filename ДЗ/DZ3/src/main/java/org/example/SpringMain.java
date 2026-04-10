package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringMain {

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }

    @Bean
    CommandLineRunner springJpaSelect(UserRepository userRepository) {
        return args -> {
            System.out.println("Spring JPA SELECT result:");
            userRepository.findAll().forEach(user ->
                    System.out.printf("id=%d, name=%s, email=%s%n", user.getId(), user.getName(), user.getEmail())
            );
        };
    }
}
