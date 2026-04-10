package org.example.jpa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaExamplesApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "app.run-examples", havingValue = "true", matchIfMissing = true)
    CommandLineRunner runExamples(JpaJdbcParityService service) {
        return args -> service.runAllExamples();
    }
}
