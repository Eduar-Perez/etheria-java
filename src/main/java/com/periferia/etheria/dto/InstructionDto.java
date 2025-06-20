package com.periferia.etheria.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructionDto {
	
	private Long id;
	private String name;
	private String instruction;
	private String description;
	private Boolean general;
	private String idUser;
	private String action;
}
