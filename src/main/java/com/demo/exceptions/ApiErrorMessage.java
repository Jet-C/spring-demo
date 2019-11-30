package com.demo.exceptions;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiErrorMessage {

	private String errorMessage;
	private String requestingURI;
	private Instant timeStamp;

	public ApiErrorMessage(String messageError, String URI, Instant timeStamp) {
		this.errorMessage = messageError;
		this.requestingURI = URI;
		this.timeStamp = timeStamp;
	}
}
