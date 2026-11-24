package com.pfc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThindeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThindeskApplication.class, args);
    }
}
