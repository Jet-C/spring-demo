package com.demo.exceptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
// TODO double check error responses
	@ExceptionHandler({ VehicleNotFoundException.class })
	public final ResponseEntity<Object> handleVehicleException(VehicleNotFoundException ex, WebRequest request) {
		List<String> errorDetailList = new ArrayList<>();
		errorDetailList.add(getErrorTimeAndDateFormatted());
		errorDetailList.add(request.getRemoteUser());
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getMessage(), request.getDescription(false),
				errorDetailList);
		return new ResponseEntity<Object>(ApiErrorMessage, ex.getStatusCode());
	}

	@ExceptionHandler({ HttpClientErrorException.class })
	public final ResponseEntity<Object> handleVehicleConflictException(HttpClientErrorException ex,
			WebRequest request) {
		List<String> errorDetailList = new ArrayList<>();
		errorDetailList.add(getErrorTimeAndDateFormatted());
		errorDetailList.add(request.getSessionId());
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				errorDetailList);
		return new ResponseEntity<Object>(ApiErrorMessage, ex.getStatusCode());
	}

	@ExceptionHandler({ Exception.class })
	public final ResponseEntity<Object> handleVehicleGeneralException(Exception ex, WebRequest request) {
		List<String> errorDetailList = new ArrayList<>();
		errorDetailList.add(getErrorTimeAndDateFormatted());
		errorDetailList.add(request.getSessionId());
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(true),
				errorDetailList);
		return new ResponseEntity<Object>(ApiErrorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<String> errorDetailList = new ArrayList<>();
		errorDetailList.add(getErrorTimeAndDateFormatted());
		errorDetailList.add(headers.getETag());
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				errorDetailList);
		return new ResponseEntity<Object>(ApiErrorMessage, status);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errorDetailList = new ArrayList<>();
		errorDetailList.add(getErrorTimeAndDateFormatted());
		errorDetailList.add(headers.getETag());
		ApiErrorMessage ApiErrorMessage = new ApiErrorMessage(ex.getLocalizedMessage(), request.getDescription(false),
				errorDetailList);
		return new ResponseEntity<Object>(ApiErrorMessage, status);

	}

	private static String getErrorTimeAndDateFormatted() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}