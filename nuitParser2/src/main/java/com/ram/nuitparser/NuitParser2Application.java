package com.ram.nuitparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NuitParser2Application {
    private static final Logger logger = LoggerFactory.getLogger(NuitParser2Application.class);

    public static void main(String[] args) {
        logger.info("Starting NuitParser2 application");
        SpringApplication.run(NuitParser2Application.class, args);
        logger.info("Application started successfully");
    }
}