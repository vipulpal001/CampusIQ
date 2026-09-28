package com.campusiq.campusiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.campusiq", "com.campusiq.campusiq"})
public class CampusiqApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusiqApplication.class, args);
    }
}