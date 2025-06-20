package com.periferia.etheria.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructionEntity {

	private Long id;
	private String name;
	private String instruction;
	private String description;
	private Boolean general;
	private String idUser;

}
