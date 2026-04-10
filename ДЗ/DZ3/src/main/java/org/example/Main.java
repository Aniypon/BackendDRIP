package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class Main {

    public static void main(String[] args) {
        HibernateSelectRunner.run();

        ConfigurableApplicationContext context = SpringApplication.run(SpringMain.class, args);
        SpringApplication.exit(context);
    }
}
