package com.periferia.etheria.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {

	private Integer statusCode;
    private String message;
    private T data;
	
    public Response(Integer statusCode, String message, T data) {
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}
    
    
	
}
