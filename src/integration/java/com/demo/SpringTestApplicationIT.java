package com.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.demo.dto.Vehicle;

/*
 * @SpringBootTest - can be used when we need to bootstrap the entire container.
 * @ActiveProfiles - Select profile configurations. Will use (application-integration.properties)
 */

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = SpringTestApplication.class)
@ActiveProfiles("integration")
public class SpringTestApplicationIT {

	private int port = 8080;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getAllVehicles_ReturnsAllVehicles_OK() {

		ResponseEntity<List<Vehicle>> responseEntity = this.restTemplate.exchange(
				"http://localhost:" + port + "/demo/vehicles", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Vehicle>>() {
				});

		List<Vehicle> vehiclesResponseList = responseEntity.getBody();

		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals(8, vehiclesResponseList.size());
		assertTrue(vehiclesResponseList.stream().anyMatch((vehicle) -> {
			return vehicle.getVin().equals("FR4EDED2150RFT5GE");
		}));
	}

	@Test
	public void getVehicleById_ReturnsVehicle_OK() {

		ResponseEntity<Vehicle> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/demo/vehicles/FR4EDED2150RFT5GE", Vehicle.class);

		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals("FR4EDED2150RFT5GE", responseEntity.getBody().getVin());
		assertEquals("Ford", responseEntity.getBody().getMake());
		assertEquals("Ranger", responseEntity.getBody().getModel());
		assertEquals(new Integer(1992), responseEntity.getBody().getYear());
	}

}