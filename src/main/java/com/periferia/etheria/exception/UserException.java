package com.periferia.etheria.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

	private static final long serialVersionUID = 1L; 
	private final int errorCode;
	private final String errorDetail;

	public UserException(String message, int errorCode, String errorDetail) {
		super(message);
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}
}
