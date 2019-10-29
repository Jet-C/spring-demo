package com.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class VehicleNotFoundException extends HttpStatusCodeException {

	private static final long serialVersionUID = 73263616501570402L;

	public VehicleNotFoundException() {
		super(HttpStatus.NOT_FOUND);
	}

	public VehicleNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
