package org.example.jpa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaExamplesApplication.class, args);
    }

    @Bean
    CommandLineRunner runExamples(JpaJdbcParityService service) {
        return args -> service.runAllExamples();
    }
}
