package com.surepay.example.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AssignmentApplication {

	private static Logger logger = LoggerFactory.getLogger(AssignmentApplication.class);

	/**
	 * main entry point, initializes the Spring Boot microservice
	 */
	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}

	/**
	 * executes at startup
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			logger.info("AssignmentApplication started");
		};
	}

}