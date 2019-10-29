package com.demo.exceptions;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiErrorMessage {

	private String errorMessage;
	private String requestingURI;
	private List<String> errorDetails;

	public ApiErrorMessage(String messageError, String URI, List<String> errorDetails) {
		this.errorMessage = messageError;
		this.requestingURI = URI;
		this.errorDetails = errorDetails;
	}
}
