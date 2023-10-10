package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sample")
public class CollectionPerTenancyMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectionPerTenancyMongoApplication.class, args);
    }

}
