package com.demo;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class SpringTestApplication {
	/*
	 * Are you up?? - http://localhost:8080/actuator/health
	 * Wanna look at some tables?? - http://localhost:8080/h2-console/
	 * Got Swag?? - http://localhost:8080/swagger-ui.html
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringTestApplication.class, args);
	}

	/*
	 * Pass the beans please...
	 * ..take a look at all the beans loaded into the app context
	 */
	@Bean
	@Profile("!test & !integration")
	public CommandLineRunner run(ApplicationContext appContext) {
		return args -> {
			String[] beans = appContext.getBeanDefinitionNames();
			Arrays.stream(beans).sorted().forEach(System.out::println);
		};
	}
}
