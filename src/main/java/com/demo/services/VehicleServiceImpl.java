package com.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.demo.dto.Vehicle;
import com.demo.exceptions.VehicleNotFoundException;
import com.demo.repository.VehicleRepository;

@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	VehicleRepository vehicleRepository;

	@Override
	public List<Vehicle> getAllVehicles() {
		return vehicleRepository.findAll();
	}

	@Override
	public Optional<Vehicle> getVehicleByVin(String vin) {
		return vehicleRepository.findById(vin);
	}

	@Override
	public Vehicle createVehicle(Vehicle newVehicle) {

		validateVehicleHelper(newVehicle);
		newVehicle.setIs_older((newVehicle.getYear() < 1981) ? true : false);

		return vehicleRepository.save(newVehicle);
	}

	@Override
	public Vehicle updateVehicle(String vin, Vehicle vehicleUpdate) {

		if (!vin.equals(vehicleUpdate.getVin())) {
			throw new HttpClientErrorException(HttpStatus.CONFLICT, "Vin in URI does not match vehicle vin to update");
		}

		Optional<Vehicle> op = vehicleRepository.findById(vin);

		if (!op.isPresent()) {
			throw new VehicleNotFoundException("Vehicle with VIN (" + vin + ") not found!");
		}
		Vehicle orginalVehicle = op.get();

		BeanUtils.copyProperties(vehicleUpdate, orginalVehicle);

		return vehicleRepository.save(orginalVehicle);
	}

	@Override
	public void deleteVehicle(String vin) {

		Optional<Vehicle> vehicle = vehicleRepository.findById(vin);

		if (vehicle.isPresent()) {
			throw new VehicleNotFoundException("Vehicle with VIN (" + vin + ") not found!");
		}

		vehicleRepository.delete(vehicle.get());
	}

	private static void validateVehicleHelper(Vehicle vehicle) {
		int vinLength = vehicle.getVin().length();
		/*
		 * Fun Fact: Prior to 1981, VINs varied in length from 11 to 17 characters. Auto
		 * checking on vehicles older than 1981 can resulted in limited info.
		 */
		if (vinLength > 17 || vinLength < 11) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "VIN is an invalid length");
		}
		if (vinLength < 17 && vehicle.getYear() >= 1981) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "VIN length is invalid for the declared year");
		}
	}
}