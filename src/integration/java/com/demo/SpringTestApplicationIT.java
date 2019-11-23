package com.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.demo.dto.Vehicle;
import com.demo.repository.VehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * @SpringBootTest - can be used when we need to bootstrap the entire container.
 * @ActiveProfiles - Select profile configurations. Will use (application-integration.properties)
 */

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = SpringTestApplication.class)
@ActiveProfiles("integration")
public class SpringTestApplicationIT {

	final private int port = 8080;
	final private String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Test
	public void GET_AllVehicles_ReturnsAllVehicles_OK() {

		ResponseEntity<List<Vehicle>> responseEntity = this.restTemplate.exchange(baseUrl + port + "/demo/vehicles",
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Vehicle>>() {
				});

		List<Vehicle> vehiclesResponseList = responseEntity.getBody();

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(8, vehiclesResponseList.size());
		assertTrue(vehiclesResponseList.stream().anyMatch((vehicle) -> {
			return vehicle.getVin().equals("FR4EDED2150RFT5GE");
		}));
	}

	@Test
	public void GET_VehicleById_ReturnsVehicle_OK() {

		ResponseEntity<Vehicle> responseEntity = this.restTemplate
				.getForEntity(baseUrl + port + "/demo/vehicles/FR4EDED2150RFT5GE", Vehicle.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("FR4EDED2150RFT5GE", responseEntity.getBody().getVin());
		assertEquals("Ford", responseEntity.getBody().getMake());
		assertEquals("Ranger", responseEntity.getBody().getModel());
		assertEquals(new Integer(1992), responseEntity.getBody().getYear());
	}

	@Test
	public void GET_VehicleById_NotFound_404() {

		// We are expecting an string error message in JSON
		ResponseEntity<String> result = this.restTemplate.exchange(baseUrl + port + "/demo/vehicles/MISSING-VIN123456",
				HttpMethod.GET, null, String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonTree = null;
		try {
			jsonTree = mapper.readTree(result.getBody());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		JsonNode jsonNode = jsonTree.get("errorMessage");

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		// Ensure the proper error message is set back to the client
		assertTrue(jsonNode.asText().contains("404 Vehicle with VIN (MISSING-VIN123456) not found"));
	}

	@Test
	public void POST_createNewVehicle_Returns_201_Created() {

		ResponseEntity<Vehicle> responseEntity = null;

		// Create new vehicle
		Vehicle newVehicle = Vehicle.builder().vin("X0RF654S54A65E66E").make("Toyota").model("Supra").year(2020)
				.is_older(false).build();

		ObjectMapper mapper = new ObjectMapper();
		String vehicleJSONString = null;
		// Our post consumes JSON format
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			vehicleJSONString = mapper.writeValueAsString(newVehicle);

			HttpEntity<String> request = new HttpEntity<String>(vehicleJSONString, headers);
			responseEntity = this.restTemplate.postForEntity(baseUrl + port + "/demo/create/vehicle", request,
					Vehicle.class);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Post request should return the newly created entity back to the client
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals("X0RF654S54A65E66E", responseEntity.getBody().getVin());
		assertEquals("Toyota", responseEntity.getBody().getMake());
		assertEquals("Supra", responseEntity.getBody().getModel());

		// Lets ensure this new vehicle has been stored in our embedded H2 db
		Optional<Vehicle> op = vehicleRepository.findById("X0RF654S54A65E66E");
		assertTrue(op.isPresent());
		assertEquals("X0RF654S54A65E66E", op.get().getVin());
	}

	@Test
	public void PUT_updateVehicle_Returns_202_Accepted() {

		ResponseEntity<Vehicle> responseEntity = null;

		// Update vehicle. Need to update to the correct year 1992 -> 1996
		Vehicle vehicleUpdate = Vehicle.builder().vin("FR4EDED2150RFT5GE").make("Ford").model("Ranger").year(1996)
				.is_older(false).build();

		ObjectMapper mapper = new ObjectMapper();
		String vehicleJSONString = null;
		// Our update consumes JSON format
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			vehicleJSONString = mapper.writeValueAsString(vehicleUpdate);

			HttpEntity<String> requestEntity = new HttpEntity<String>(vehicleJSONString, headers);

			responseEntity = this.restTemplate.exchange(baseUrl + port + "/demo/update/vehicle/FR4EDED2150RFT5GE",
					HttpMethod.PUT, requestEntity, Vehicle.class);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Put request should return the updated vehicle entity back to the client
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
		assertEquals("FR4EDED2150RFT5GE", responseEntity.getBody().getVin());
		assertEquals("Ford", responseEntity.getBody().getMake());
		assertEquals("Ranger", responseEntity.getBody().getModel());
		assertEquals(new Integer(1996), responseEntity.getBody().getYear());
		assertFalse(responseEntity.getBody().getIs_older());
	}

	@Test
	public void DELETE_vehicleById_NoContent_204() {

		ResponseEntity<Object> responseEntity = this.restTemplate
				.exchange(baseUrl + port + "/demo/vehicles/GMDE65A5ED66ER002", HttpMethod.DELETE, null, Object.class);

		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
		assertNull(responseEntity.getBody());

		// Lets ensure the vehicle has been deleted from our embedded H2 db
		Optional<Vehicle> optional = vehicleRepository.findById("GMDE65A5ED66ER002");
		assertFalse(optional.isPresent());
	}

}