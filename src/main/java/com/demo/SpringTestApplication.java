package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTestApplication {

	public static void main(String[] args) {
		// Are you up?? - http://localhost:8080/actuator/health
		SpringApplication.run(SpringTestApplication.class, args);
	}

}
