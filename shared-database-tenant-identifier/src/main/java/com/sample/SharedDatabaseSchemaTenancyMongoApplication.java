package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sample")
public class SharedDatabaseSchemaTenancyMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharedDatabaseSchemaTenancyMongoApplication.class, args);
	}

}
