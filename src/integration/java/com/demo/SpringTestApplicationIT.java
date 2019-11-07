package com.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scripting.ScriptSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.demo.dto.Vehicle;

/*
 * @Sql - Loads SQL scripts defined under this relative path. Run script to populate our tables for testing
 * @SpringBootTest - can be used when we need to bootstrap the entire container. 
 */

// The annotation works by creating the ApplicationContext that will be utilized in our tests.
//https://www.petrikainulainen.net/programming/gradle/getting-started-with-gradle-integration-testing/

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = SpringTestApplication.class)
//@Sql(scripts = "vehicle_test.sql")
@Sql({"classpath:vehicle_test_2.sql"}) //"vehicle_test.sql", 
@ActiveProfiles("test")
public class SpringTestApplicationIT {

	private int port = 8080;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getAllVehicles_ReturnsAllVehicles_OK() {
		ClassPathResource cResource = new ClassPathResource("vehicle_test.sql", getClass());
		System.out.print("\n\n\n");
		System.out.println(cResource.toString());
		System.out.println(cResource.getClassLoader());
		System.out.print("\n\n\n");
		ClassPathResource cResource2= new ClassPathResource("vehicle_test_2.sql", getClass());
		System.out.print("\n\n\n");
		System.out.println(cResource2.toString());
		System.out.println(cResource2.getClassLoader());
		System.out.print("\n\n\n");

		ResponseEntity<List<Vehicle>> responseEntity = this.restTemplate.exchange(
				"http://localhost:" + port + "/demo/vehicles", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Vehicle>>() {
				});

		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void getAllVehicles_ReturnsAllVehicle_OK() {

		ResponseEntity<Vehicle> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/demo/vehicles/FR4EDED2150RFT5GE", Vehicle.class);
		System.out.print("\n\n\n\n\n\n" + responseEntity.getBody().toString());
		System.out.print(responseEntity.getStatusCode());
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
	}

}