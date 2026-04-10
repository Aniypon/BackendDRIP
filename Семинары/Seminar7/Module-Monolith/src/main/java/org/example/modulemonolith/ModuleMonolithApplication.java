package org.example.modulemonolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Modulith
@EnableAsync
public class ModuleMonolithApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleMonolithApplication.class, args);
    }
}
