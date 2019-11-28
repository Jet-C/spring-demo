package com.demo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.demo.controllers.VehicleController;
import com.demo.dto.Vehicle;
import com.demo.exceptions.VehicleNotFoundException;
import com.demo.services.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * @WebMvcTest - for testing the controller layer exclusively
 * Includes both the @AutoConfigureWebMvc and the @AutoConfigureMockMvc, among other functionality.
 * @ExtendWith - SpringExtension integrates the Spring TestContext Framework into JUnit 5's Jupiter programming model.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(VehicleController.class)
@ActiveProfiles("test")
public class DemoWebLayerTest {

	/*
	 * We can @Autowire MockMvc because the WebApplicationContext provides an
	 * instance/bean for us
	 */
	@Autowired
	MockMvc mockMvc;

	/*
	 * Jackson mapper for Object -> JSON conversion
	 */
	@Autowired
	ObjectMapper mapper;

	/*
	 * We use @MockBean because the WebApplicationContext does not provide an
	 * instance/bean of this service in its context. It only loads the beans solely
	 * required for testing the controller
	 */
	@MockBean
	VehicleService vechicleService;

	@Test
	public void get_allVehicles_returnsOkWithListOfVehicles() throws Exception {

		List<Vehicle> vehicleList = new ArrayList<>();
		Vehicle vehicle1 = new Vehicle("AD23E5R98EFT3SL00", "Ford", "Fiesta", 2016, false);
		Vehicle vehicle2 = new Vehicle("O90DEPADE564W4W83", "Volkswagen", "Jetta", 2016, false);
		vehicleList.add(vehicle1);
		vehicleList.add(vehicle2);

		// Mocking out the vehicle service
		Mockito.when(vechicleService.getAllVehicles()).thenReturn(vehicleList);

		mockMvc.perform(MockMvcRequestBuilders.get("/demo/vehicles").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].vin", is("AD23E5R98EFT3SL00"))).andExpect(jsonPath("$[0].make", is("Ford")))
				.andExpect(jsonPath("$[1].vin", is("O90DEPADE564W4W83")))
				.andExpect(jsonPath("$[1].make", is("Volkswagen")));
	}

	@Test
	public void post_createsNewVehicleAndReturnsObjWith201() throws Exception {
		Vehicle vehicle = new Vehicle("AD23E5R98EFT3SL00", "Ford", "Fiesta", 2016, false);

		Mockito.when(vechicleService.createVehicle(Mockito.any(Vehicle.class))).thenReturn(vehicle);

		// Build post request with vehicle object payload
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/demo/create/vehicle")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8").content(this.mapper.writeValueAsBytes(vehicle));

		mockMvc.perform(builder).andExpect(status().isCreated()).andExpect(jsonPath("$.vin", is("AD23E5R98EFT3SL00")))
				.andExpect(MockMvcResultMatchers.content().string(this.mapper.writeValueAsString(vehicle)));
	}

	@Test
	public void post_submitsInvalidVehicle_WithEmptyMake_Returns400() throws Exception {
		// Create new vehicle with empty 'make' field
		Vehicle vehicle = new Vehicle("AD23E5R98EFT3SL00", "", "Firebird", 1982, false);

		String vehicleJsonString = this.mapper.writeValueAsString(vehicle);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/demo/create/vehicle/")
				.contentType(MediaType.APPLICATION_JSON).content(vehicleJsonString)).andExpect(status().isBadRequest());

		// @Valid annotation in controller will cause exception to be thrown
		assertEquals(MethodArgumentNotValidException.class,
				resultActions.andReturn().getResolvedException().getClass());
		assertTrue(resultActions.andReturn().getResolvedException().getMessage().contains("'make' field was empty"));
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith202() throws Exception {
		Vehicle vehicle = new Vehicle("AD23E5R98EFT3SL00", "Ford", "Fiesta", 2016, false);

		Mockito.when(vechicleService.updateVehicle("AD23E5R98EFT3SL00", vehicle)).thenReturn(vehicle);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.put("/demo/update/vehicle/AD23E5R98EFT3SL00", vehicle).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.content(this.mapper.writeValueAsBytes(vehicle));

		mockMvc.perform(builder).andExpect(status().isAccepted()).andExpect(jsonPath("$.vin", is("AD23E5R98EFT3SL00")))
				.andExpect(MockMvcResultMatchers.content().string(this.mapper.writeValueAsString(vehicle)));
	}

	@Test
	public void delete_deleteVehicle_Returns204Status() throws Exception {
		String vehicleVin = "AD23E5R98EFT3SL00";

		// Using Spy to partially mock the deleteVehicle method
		VehicleService serviceSpy = Mockito.spy(vechicleService);
		Mockito.doNothing().when(serviceSpy).deleteVehicle(vehicleVin);

		mockMvc.perform(MockMvcRequestBuilders.delete("/demo/vehicles/AD23E5R98EFT3SL00")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

		verify(vechicleService, times(1)).deleteVehicle(vehicleVin);
	}

	@Test
	public void get_vehicleByVin_ThrowsVehicleNotFoundException() throws Exception {

		// Return an empty Optional object since we didn't find the vin
		Mockito.when(vechicleService.getVehicleByVin("AD23E5R98EFT3SL00")).thenReturn(Optional.empty());

		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get("/demo/vehicles/AD23E5R98EFT3SL00").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		assertEquals(VehicleNotFoundException.class, resultActions.andReturn().getResolvedException().getClass());
		assertTrue(resultActions.andReturn().getResolvedException().getMessage()
				.contains("Vehicle with VIN (" + "AD23E5R98EFT3SL00" + ") not found!"));
	}
}