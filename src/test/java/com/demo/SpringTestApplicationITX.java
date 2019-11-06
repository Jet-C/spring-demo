//package com.demo;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scripting.ScriptSource;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import com.demo.dto.Vehicle;
//
///*
// * @Sql - Loads SQL scripts defined under this relative path. Run script to populate our tables for testing
// */
//// @SpringBootTest can be used when we need to bootstrap the entire container. 
//// The annotation works by creating the ApplicationContext that will be utilized in our tests.
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = SpringTestApplication.class)
////@Sql(scripts = "vehicle_test.sql") //scripts = "/test-schema.sql",
////https://www.petrikainulainen.net/programming/gradle/getting-started-with-gradle-integration-testing/
////@ActiveProfiles("test") // NOT NEEDED
////@ExtendWith(SpringExtension.class)  // NOT NEEDED
//public class SpringTestApplicationITX {
//
//	private int port = 8080;
//
//	@Autowired
//	private TestRestTemplate restTemplate;
//
//	@Test
//	public void getAllVehicles_ReturnsAllVehicles_OK() {
//		// String body = this.restTemplate.getForObject("/demo/vehicles", String.class);
//		ClassPathResource cResource = new ClassPathResource("vehicle_test.sql", getClass());
//		System.out.print("\n\n\n");
//		System.out.println(cResource.toString());
//		System.out.println(cResource.getClassLoader());
//
//		System.out.print("\n\n\n");
//
//		ResponseEntity<List> responseEntity = this.restTemplate
//				.getForEntity("http://localhost:" + port + "/demo/vehicles", List.class);
//
//		System.out.print(responseEntity.getStatusCode() + "\n\n\n\n\n\n");
//		System.out.print(responseEntity.getBody().toString());
//		assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
//	}
//
////	@Test
////	public void getAllVehicles_ReturnsAllVehicle_OK() {
////		// String body = this.restTemplate.getForObject("/demo/vehicles", String.class);
////
////		ResponseEntity<Vehicle> responseEntity = this.restTemplate
////				.getForEntity("http://localhost:" + port + "/demo/vehicles/FR4EDED2150RFT5GE", Vehicle.class);
////		System.out.print("\n\n\n\n\n\n" + responseEntity.getBody().toString());
////		System.out.print(responseEntity.getStatusCode());
////		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
////	}
//
//}