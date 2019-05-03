package com.example.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@PropertySource({ "classpath:application.properties", "classpath:database.properties"})
@SpringBootApplication
@EnableJpaAuditing
public class TutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(TutorApplication.class, args);
    }
}