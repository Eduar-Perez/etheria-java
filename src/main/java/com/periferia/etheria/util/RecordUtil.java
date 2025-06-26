package com.periferia.etheria.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.entity.RecordEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordUtil {

	private RecordUtil() {}

	public static RecordEntity convertDtoToEntity(RecordDto recordDto) {
		RecordEntity recordEntity = new RecordEntity();
		recordEntity.setId(recordDto.getId());
		recordEntity.setQuestion(recordDto.getQuestion());
		recordEntity.setResponse(recordDto.getResponse());

		return recordEntity;
	}

	public static List<RecordDto> convertEntityToDtoList(List<RecordEntity> recordEntityList) {
		List<RecordDto> response = new ArrayList<>();
		for(RecordEntity recordEntity : recordEntityList) {
			RecordDto recordDto = new RecordDto(null, null, null);
			recordDto.setId(recordEntity.getId()); 
			recordDto.setQuestion(recordEntity.getQuestion());
			recordDto.setResponse(recordEntity.getResponse());
			response.add(recordDto);
		}

		return response;
	}

	public static RecordDto convertEntityToDto(RecordEntity recordEntity) {
		return new RecordDto(
				recordEntity.getId(), 
				recordEntity.getQuestion(), 
				recordEntity.getResponse());
	}

}
