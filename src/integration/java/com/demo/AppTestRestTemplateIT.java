package com.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 * @SpringBootTest - Run our app in as a test context enabling @Test methods.
 * @ActiveProfiles - Select profile configurations. We will use the (application-'integration'.properties)
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = SpringTestApplication.class)
@ActiveProfiles("integration")
public class AppTestRestTemplateIT {

	final private static int port = 8080;
	final private static String baseUrl = "http://localhost:";

	/*
	 * @SpringBootTest registers a TestRestTeplate bean so we can directly @Autowire
	 * it in
	 */
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Test
	public void get_allVehicles_ReturnsAllVehicles_OK() {

		List<String> expectedVINList = Stream.of("FR45212A24D4SED66", "FR4EDED2150RFT5GE", "XDFR6545DF3A5R896",
				"XDFR64AE9F3A5R78S", "PQERS2A36458E98CD", "194678S400005", "48955460210").collect(Collectors.toList());

		ResponseEntity<List<Vehicle>> responseEntity = this.restTemplate.exchange(baseUrl + port + "/demo/vehicles",
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Vehicle>>() {
				});

		List<Vehicle> vehiclesResponseList = responseEntity.getBody();

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertTrue(vehiclesResponseList.size() > 7);
		assertTrue(vehiclesResponseList.stream().anyMatch((vehicle) -> {
			return expectedVINList.contains(vehicle.getVin());
		}));
	}

	@Test
	public void get_vehicleById_Returns_Vehicle_OK() {

		ResponseEntity<Vehicle> responseEntity = this.restTemplate
				.getForEntity(baseUrl + port + "/demo/vehicles/48955460210", Vehicle.class);

		Vehicle expectedVehicle = Vehicle.builder().vin("48955460210").make("Ford").model("Mustang").year(1974)
				.is_older(true).build();

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(expectedVehicle, responseEntity.getBody());
	}

	@Test
	public void get_vehicleById_Returns_NotFound_404() {

		// We are expecting an string error message in JSON
		ResponseEntity<String> result = this.restTemplate.exchange(baseUrl + port + "/demo/vehicles/MISSING-VIN123456",
				HttpMethod.GET, null, String.class);

		// Parse JSON message response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonTree = null;
		try {
			jsonTree = mapper.readTree(result.getBody());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		JsonNode jsonNode = jsonTree.get("errorMessage");

		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		// Assert the proper error message is received
		assertTrue(jsonNode.asText().contains("404 Vehicle with VIN (MISSING-VIN123456) not found"));
	}

	@Test
	public void post_createNewVehicle_Returns_201_Created() {

		// Create a new vehicle
		Vehicle newVehicle = Vehicle.builder().vin("X0RF654S54A65E66E").make("Toyota").model("Supra").year(2020)
				.is_older(false).build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Vehicle> request = new HttpEntity<Vehicle>(newVehicle, headers);

		ResponseEntity<Vehicle> responseEntity = this.restTemplate
				.postForEntity(baseUrl + port + "/demo/create/vehicle", request, Vehicle.class);

		// Post request should return the newly created entity back to the client
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals("X0RF654S54A65E66E", responseEntity.getBody().getVin());
		assertEquals("Toyota", responseEntity.getBody().getMake());
		assertEquals("Supra", responseEntity.getBody().getModel());
		assertFalse(responseEntity.getBody().getIs_older());

		// Double check this new vehicle has been stored in our embedded H2 db
		Optional<Vehicle> op = vehicleRepository.findById("X0RF654S54A65E66E");
		assertTrue(op.isPresent());
		assertEquals("X0RF654S54A65E66E", op.get().getVin());
	}

	@Test
	public void post_createNewVehicle_Returns_400_BadRequest() {

		ResponseEntity<String> result = null;

		// Create new vehicle with a bad VIN length for the declared model year
		Vehicle newVehicle = Vehicle.builder().vin("BAD-LENGTH-VIN").make("Chevrolet").model("Camaro").year(2018)
				.is_older(false).build();

		// We'll use an object mapper to show our HttpEntity also accepts JSON string
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		// Our post consumes JSON format
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			String vehicleJSONString = mapper.writeValueAsString(newVehicle);

			HttpEntity<String> request = new HttpEntity<String>(vehicleJSONString, headers);
			result = this.restTemplate.postForEntity(baseUrl + port + "/demo/create/vehicle", request, String.class);
			// Our JSON error message has an "errorMessage" attribute
			jsonNode = mapper.readTree(result.getBody()).get("errorMessage");

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
		// Assert the expected error message
		assertTrue(jsonNode.asText().contains("VIN length is invalid for the declared year"));
	}

	@Test
	public void put_updateVehicle_Returns_202_Accepted() {

		// Update vehicle. Need to update to the correct year '1992' -> '1996'
		Vehicle vehicleUpdate = Vehicle.builder().vin("FR4EDED2150RFT5GE").make("Ford").model("Ranger").year(1996)
				.is_older(false).build();

		// Our targeted URI consumes JSON format
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Vehicle> requestEntity = new HttpEntity<Vehicle>(vehicleUpdate, headers);

		ResponseEntity<Vehicle> responseEntity = this.restTemplate.exchange(
				baseUrl + port + "/demo/update/vehicle/FR4EDED2150RFT5GE", HttpMethod.PUT, requestEntity,
				Vehicle.class);

		// Put request should return the updated vehicle entity back to the client
		assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
		assertEquals(vehicleUpdate, responseEntity.getBody());
	}

	@Test
	public void delete_vehicleById_Returns_NoContent_204() {

		ResponseEntity<Object> responseEntity = this.restTemplate
				.exchange(baseUrl + port + "/demo/vehicles/GMDE65A5ED66ER002", HttpMethod.DELETE, null, Object.class);

		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
		assertNull(responseEntity.getBody());

		// Double check the vehicle has been deleted from our embedded H2 db
		Optional<Vehicle> optional = vehicleRepository.findById("GMDE65A5ED66ER002");
		assertFalse(optional.isPresent());
	}

}