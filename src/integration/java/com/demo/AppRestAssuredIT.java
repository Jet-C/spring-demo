package com.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import com.demo.dto.Vehicle;

import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringTestApplication.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles({ "integration" })
public class AppRestAssuredIT {

	@Value("${local.server.port}")
	private int ports;

	@BeforeAll
	public void setUp() {
		port = ports;
		baseURI = "http://localhost/demo"; // Will result in "http://localhost:xxxx/demo"
	}

	@Test
	public void get_AllVehicles_returnsAllVehicles_200() {

		String vinArray[] = { "FR45212A24D4SED66", "FR4EDED2150RFT5GE", "XDFR6545DF3A5R896", "GMDE65A5ED66ER002",
				"PQERS2A36458E98CD", "194678S400005", "48955460210" };

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).when().get("/vehicles").then();

		System.out.println("'getAllVehicles_returnsAllVehicles_200()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.OK.value()).body("content.size()", greaterThan(7))
				.body(containsString("Corvette")).body("find {it.vin == 'FR45212A24D4SED66'}.year", equalTo(2010))
				.body("find {it.vin == 'FR45212A24D4SED66'}.year", equalTo(2010))
				.body("make", hasItems("Ford", "Chevrolet", "Toyota", "Nissan")).body("make", not(hasItem("Honda")))
				.body("vin", hasItems(vinArray)).body("findAll {it.year < 1990}.size()", is(2));
	}

	@Test
	public void get_VehicleById_returnsVehicle_200() {

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).when().get("/vehicles/FR45212A24D4SED66").then();

		System.out.println("'getVehicleById_returnsVehicle_200()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.OK.value()).body("make", equalTo("Ford"))
				.body("model", equalTo("F-150")).body("year", equalTo(2010)).body("is_older", equalTo(false));
	}

	@Test
	public void get_VehicleById_returnsNotFound_404() {

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).when().get("/vehicles/NON-EXISTING-ID77").then();

		System.out.println("'get_VehicleById_returnsNotFound_404()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.NOT_FOUND.value()).body("errorMessage",
				containsString("404 Vehicle with VIN (NON-EXISTING-ID77) not found"));
	}

	@Test
	public void post_newVehicle_returnsCreatedVehicle_201() {
		// Build new vehicle to post
		Vehicle newVehicle = Vehicle.builder().vin("X0RF654S54A65E66E").make("Toyota").model("Supra").year(2020)
				.is_older(false).build();

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).body(newVehicle).when().post("/create/vehicle").then();

		System.out.println("'post_Vehicle_returnsCreatedVehicle_201()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.CREATED.value()).body("vin", equalTo("X0RF654S54A65E66E"))
				.body("make", equalTo("Toyota")).body("model", equalTo("Supra")).body("year", equalTo(2020))
				.body("is_older", equalTo(false));
	}

	@Test
	public void post_newVehicle_Returns_BadRequest_400() {
		// Create new vehicle with a bad VIN length for the declared model year
		Vehicle newVehicle = Vehicle.builder().vin("BAD-LENGTH-VIN").make("Chevrolet").model("Camaro").year(2018)
				.is_older(false).build();

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).body(newVehicle).when().post("/create/vehicle").then();

		System.out.println("'post_newVehicle_Returns_BadRequest_400()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.BAD_REQUEST.value()).body("errorMessage",
				containsString("VIN length is invalid for the declared year"));
	}

	@Test
	public void put_updateVehicle_returnsUpdatedVehicle_202() {
		// Update the year on the vehicle 1992 -> 1997
		Vehicle updateVehicle = Vehicle.builder().vin("FR4EDED2150RFT5GE").make("Ford").model("Ranger").year(1997)
				.is_older(false).build();

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).body(updateVehicle).when()
				.put("/update/vehicle/FR4EDED2150RFT5GE").then();

		System.out
				.println("'put_updateVehicle_returnsUpdatedVehicle_202()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.ACCEPTED.value()).body("vin", equalTo("FR4EDED2150RFT5GE"))
				.body("make", equalTo("Ford")).body("model", equalTo("Ranger")).body("year", equalTo(1997))
				.body("is_older", equalTo(false));
	}

	@Test
	public void delete_vehicle_returnsNoContent_204() {

		ValidatableResponse response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON_VALUE).when().delete("/vehicles/XDFR64AE9F3A5R78S").then();

		System.out.println("'delete_vehicle_returnsNoContent_204()' response:\n" + response.extract().asString());

		response.assertThat().statusCode(HttpStatus.NO_CONTENT.value());
	}

}
