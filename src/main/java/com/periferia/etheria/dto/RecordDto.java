package com.periferia.etheria.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordDto {

	private Long id;
	private String question;
	private String response;

	public RecordDto(Long id, String question, String response) {
		super();
		this.id = id;
		this.question = question;
		this.response = response;
	}


}
