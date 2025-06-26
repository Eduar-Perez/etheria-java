package com.periferia.etheria.entity;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TitleRecordEntity {
	
	private Long id;
	private String title;
	private LocalDate dateCreate;
	private String idUser;
	private String uuid;
	private List<RecordEntity> recordEntity;

}
