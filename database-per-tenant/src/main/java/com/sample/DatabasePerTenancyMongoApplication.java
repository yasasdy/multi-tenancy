package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sample")
public class DatabasePerTenancyMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabasePerTenancyMongoApplication.class, args);
	}

}
