package com.periferia.etheria.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RecordDto {

	private Long id;
	private String question;
	private String response;
	private LocalDate dateCreate;
	private String uuid;

	public RecordDto(Long id, String question, String response, LocalDate dateCreate, String uuid) {
		super();
		this.id = id;
		this.question = question;
		this.response = response;
		this.dateCreate = dateCreate;
		this.uuid = uuid;
	}


}
