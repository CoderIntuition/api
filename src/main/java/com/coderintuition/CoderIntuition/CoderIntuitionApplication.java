package com.coderintuition.CoderIntuition;

import com.coderintuition.CoderIntuition.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class CoderIntuitionApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoderIntuitionApplication.class, args);
    }
}
