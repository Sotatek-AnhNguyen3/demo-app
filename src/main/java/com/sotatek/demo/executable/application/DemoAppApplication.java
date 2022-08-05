package com.sotatek.demo.executable.application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication(scanBasePackages = {"com.sotatek.demo"}) // disable spring security
@EnableJpaRepositories("com.sotatek.demo.domain.port")
@EntityScan("com.sotatek.demo.domain.entitiy")
public class DemoAppApplication {
	private static final Logger logger = LoggerFactory.getLogger(DemoAppApplication.class);

	public static void main(String[] args) {
		logger.info("action", "application_started");
		SpringApplication.run(DemoAppApplication.class, args);
	}
}
