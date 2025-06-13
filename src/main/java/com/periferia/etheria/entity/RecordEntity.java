package com.periferia.etheria.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecordEntity {

	private Long id;
	private String question;
	private String response;
	private LocalDate dateCreate;
	private String uuid;
}
