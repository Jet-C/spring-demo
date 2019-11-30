package com.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.dto.Vehicle;

/*
 * DAO methods interface. Just by extending the JpaRepository
 * we can use the concrete implementations of the most relevant query methods.
 * For example 'findById' or 'findAll'
 * Spring Data JPA uses naming conventions and reflection to generate the
 * concrete implementation of the interface we define.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

}