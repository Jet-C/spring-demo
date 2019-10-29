package com.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.dto.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

}
