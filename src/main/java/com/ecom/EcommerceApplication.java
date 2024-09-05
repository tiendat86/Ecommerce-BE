package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        System.out.println("Application is running...");
        System.out.println("Application is running 2...");
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
