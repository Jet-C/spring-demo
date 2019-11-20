package com.demo.services;

import java.util.List;
import java.util.Optional;

import com.demo.dto.Vehicle;

public interface VehicleService {

	List<Vehicle> getAllVehicles();

	Optional<Vehicle> getVehicleByVin(String vin);

	Vehicle createVehicle(Vehicle newVehicle);

	Vehicle updateVehicle(String vin, Vehicle vehicleUpdate);

	void deleteVehicle(String vin);

}
