package com.demo.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/*
 * Using lombok for auto-generation of boiler plate getters, setters, toString,
 * equals, hashcode, all-arg and no-arg constructors
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name="vehicles")
public class Vehicle {

	@Id
	@Column(name = "VIN", nullable = false, length = 17)
	@NonNull
	private String vin;

	@Column(name = "make", nullable = false)
	@NonNull
	private String make;
	@Column(name = "model", nullable = false)
	@NonNull
	private String model;
	@Column(name = "year", nullable = false)
	@NonNull
	private Integer year;
	@Column(name = "is_older", nullable= true)
	private Boolean is_older;
}
