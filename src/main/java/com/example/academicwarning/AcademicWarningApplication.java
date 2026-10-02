package com.example.academicwarning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AcademicWarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicWarningApplication.class, args);
    }

}