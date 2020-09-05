package com.coderintuition.CoderIntuition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class CoderIntuitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoderIntuitionApplication.class, args);
    }

    ExecutorService scheduler = Executors.newFixedThreadPool(5);
}
