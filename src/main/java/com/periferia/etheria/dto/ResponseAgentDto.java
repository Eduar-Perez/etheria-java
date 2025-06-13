package com.periferia.etheria.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAgentDto {
	private Long id;
	private String question;
	private String response;
	private LocalDate dateCreate;
	private String uuid;
}
