package com.periferia.etheria.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TitleRecordDto {

	private Long id;
	private String title;
	private LocalDate dateCreate;
	private String idUser;
	private String uuid;
	private List<RecordDto> recordDto;
}
