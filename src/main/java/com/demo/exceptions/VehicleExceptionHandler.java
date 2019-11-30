package com.demo.exceptions;

import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class VehicleExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ VehicleNotFoundException.class })
	public final ResponseEntity<Object> handleVehicleException(VehicleNotFoundException ex, WebRequest request) {
		Instant timeStamp = Instant.now();
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getMessage(), request.getDescription(false),
				timeStamp);
		return new ResponseEntity<Object>(ApiErrorMessage, ex.getStatusCode());
	}

	@ExceptionHandler({ HttpClientErrorException.class })
	public final ResponseEntity<Object> handleVehicleConflictException(HttpClientErrorException ex,
			WebRequest request) {
		Instant timeStamp = Instant.now();
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				timeStamp);
		return new ResponseEntity<Object>(ApiErrorMessage, ex.getStatusCode());
	}

	@ExceptionHandler({ Exception.class })
	public final ResponseEntity<Object> handleVehicleGeneralException(Exception ex, WebRequest request) {
		Instant timeStamp = Instant.now();
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(true),
				timeStamp);
		return new ResponseEntity<Object>(ApiErrorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		Instant timeStamp = Instant.now();
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				timeStamp);
		return new ResponseEntity<Object>(ApiErrorMessage, status);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Instant timeStamp = Instant.now();
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				timeStamp);
		return new ResponseEntity<Object>(ApiErrorMessage, status);
	}
}